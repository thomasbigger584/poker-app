# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository layout

Two independent modules under one repo:

- `server/` — Spring Boot backend (`server/api`, Java 21, Maven) plus one Docker Compose file per piece of infrastructure (`keycloak`, `postgres`, `rabbitmq`, `nginx`, `tailscale`). The root `server/docker-compose.yml` wires them all together; every service shares the Tailscale container's network namespace (`network_mode: service:tailscale`).
- `client/android/` — native Android app (Java/Kotlin, Gradle), package `com.twb.pokerapp`, minSdk 24 / targetSdk 35.

Backend and Android share their DTOs and the WebSocket message protocol through a single **protobuf** contract under `proto/poker/` (see *Wire contract — protobuf* below). Both modules generate code from the same `.proto` files, so changing a message or enum updates both sides at once — there is no hand-written mirror to keep in sync.

## Common commands

All backend commands run from `server/api` (the Maven module root). Use the `./mvnw` wrapper.

```shell
# Run the full stack locally (needs server/api/env/.secrets.env with TS_AUTHKEY)
cd server && docker compose up --build

# Run ALL tests — surefire + failsafe are SKIPPED unless this profile is active
cd server/api && ./mvnw clean verify -P run-tests

# Run a SINGLE test (still needs -P run-tests, or it is skipped)
cd server/api && ./mvnw test -P run-tests -Dtest=HandTypeEvaluatorTest          # unit (surefire)
cd server/api && ./mvnw verify -P run-tests -Dit.test=TexasGame3PlayerIT        # integration (failsafe)
```

Android (from `client/android`):

```shell
./gradlew assembleDebug          # build debug APK
```

### Test prerequisites (important)

- Integration tests (`*IT.java`) use **Testcontainers** to spin up Postgres, RabbitMQ, Keycloak, and the API itself. They reference locally-built images `com.twb.pokerapp/api` and `com.twb.pokerapp/rabbitmq`, so build them first (CI does this): `cd server && docker compose build rabbitmq api`. Docker must be running.
- Tests need the Keycloak realm (`server/keycloak/poker-app-realm.json`) copied onto the test classpath. Maven does this in the `generate-test-resources` phase; when running an IT directly from IntelliJ, run the `process-test-resources` goal first (see `.idea/runConfigurations`).
- Default unit-test pattern is `**/*Test.java` (surefire), integration is `**/*IT.java` (failsafe). Both are `skip=true` outside the `run-tests` profile.

## Architecture

### Game engine — one thread per table

Each active poker table runs on its own dedicated `GameThread` (`service/game/thread`). `GameThreadManager` keeps a `ConcurrentHashMap<tableId, GameThread>` and serializes create/delete per table with an `XSync<UUID>` mutex. `GameThread` extends `BaseGameThread` (which holds all the `@Autowired` service/repository dependencies) and runs the round lifecycle loop in `run()`: wait for players → create round → init → run round → finish round, checking interrupt flags (`AtomicBoolean`) between every step.

`GameType` is a generated **protobuf** enum (`com.twb.pokerapp.proto`); the strategy factory is the static `GameStrategies` (`service/game`). It maps each `GAME_TYPE_*` (`TEXAS_HOLDEM`, `BLACKJACK`) to its `GameThread`, `GamePlayerActionService`, `GamePlayerTurnService`, and `TableValidationService` beans (resolved from the `ApplicationContext`) plus its min/max player counts. To add a game variant, add a `GameType` value in `enums.proto`, add a branch to each `GameStrategies` switch, and implement those four beans under `service/game/thread/impl/<variant>`.

Player turns are coordinated with a `CountDownLatch` wrapped in `PlayerTurnLatchDTO`: the game thread blocks waiting for a turn; an inbound player action (on a different "caller" thread, marked `@CallerThread`) calls `onPostPlayerAction`, which counts the latch down. Server-pushed messages are sent **after** the DB transaction commits via `TransactionUtil.afterCommit(...)`.

There are two transaction templates: `writeTx` and `readTx` (the latter `@Qualifier("readTx")`). Use the matching one for reads vs. writes inside game logic.

### Wire contract — protobuf (DTOs + enums)

All DTOs and enums are generated from a single protobuf contract in `proto/poker/`, split by concern: `enums.proto`, `domain.proto` (core domain messages), `validation.proto`, `rest.proto` (REST request/response bodies), and `websocket.proto` (the STOMP message protocol). Every file uses `package poker`, `java_package = com.twb.pokerapp.proto`, and `java_multiple_files = true`, so each message/enum generates as `com.twb.pokerapp.proto.<Name>`. The server generates full `protobuf-java` (`protobuf-maven-plugin`, `protoSourceRoot ../../proto`); Android generates `protobuf-javalite` (Gradle protobuf plugin, `srcDir ../../../proto`).

The proto **enums are the single source of truth** — the former hand-written `domain.enumeration` enums are gone (the only survivor is the `Persona` enum, used by bots). Behavior that used to live on those enums now sits in small companion classes under `mapper/enumeration`: `Ranks`, `Suits`, `CardGroups`, `HandTypeNames`, `ActionFlow`, `RoundProgression`, `ConnectionTypes`. Note a proto enum's `values()` includes the synthetic `*_UNSPECIFIED` (0) and `UNRECOGNIZED` (-1) members — iterate the companion `VALUES` arrays (e.g. `Ranks.VALUES`) when you want only the real values, and never call `getNumber()` on `UNRECOGNIZED`.

Entities persist proto enums via JPA `AttributeConverter`s under `domain/converter` (base class `ProtoEnumStringConverter`), applied with `@Convert` (not `@Enumerated`). The converter stores the **legacy short name** (e.g. `CHECK`, `IN_PROGRESS`) — not the prefixed proto name or the int tag — so the DB schema is unchanged; `*_UNSPECIFIED`/`UNRECOGNIZED` map to `null` with a WARN. `@Query` JPQL that compares an enum column references the proto FQN (e.g. `com.twb.pokerapp.proto.RoundState.ROUND_STATE_FINISHED`) and Hibernate applies the converter.

DTO↔entity conversion is done by hand-written `@Component` mappers under `mapper/`; `ProtoConvert` holds the scalar encodings (UUID / BigDecimal / char / LocalDateTime ↔ string). A nullable enum field is left unset (its proto `*_UNSPECIFIED` default) rather than set to null.

### Hand evaluation — native JNI

`RankEvaluator` loads a native shared library (`evaluator.so`) via `System.load(System.getenv("EVALUATOR_SO_PATH"))` and calls `getRankNative(...)`. The `.so` is compiled in the `Dockerfile` from `src/main/cpp/PokerEvaluator.cpp` + the cloned [HenryRLee/PokerHandEvaluator](https://github.com/HenryRLee/PokerHandEvaluator) library. `HandTypeEvaluator` is the separate pure-Java path that classifies hand *type* (flush, straight, etc.). Hand ranking only works when `EVALUATOR_SO_PATH` points at a built `.so` (set in the Docker images).

### Real-time messaging — STOMP over WebSocket via RabbitMQ

- Clients subscribe to `/app/loops.{tableId}` — `TableWebSocketController.userSubscribed` returns initial game state.
- Broadcasts go out on `/topic/loops.{tableId}`, relayed through RabbitMQ (STOMP relay).
- Inbound client messages target `/app/pokerTable.{tableId}.<action>`: `sendPlayerAction`, `sendChatMessage`, `sendDisconnectPlayer`, `sendBotConnected` (see `TableWebSocketController`).
- Server→client messages are the generated `ServerMessageDTO` proto, whose `oneof payload` replaces the old `ServerMessageType` enum and hand-written payload DTOs (`getPayloadCase()` discriminates). `ServerMessageFactory` builds them; `MessageDispatcher` sends them as binary protobuf STOMP frames.
- The STOMP handshake `X-Connection-Type` header carries the **short** token (`PLAYER` / `LISTENER`); `ConnectionTypes` (`mapper/enumeration`) translates to/from the proto `ConnectionType` on both server and test client.

### Auth & users

Keycloak owns identity (OAuth2 resource server; the API validates JWTs). Keycloak user events arrive over RabbitMQ and are handled by `KeycloakRabbitMqConsumer` / `UserRabbitMqConsumer` to sync the local `AppUser` table.

`AppUser` is an abstract `@Entity` (`@Inheritance(InheritanceType.JOINED)`, table `app_user`) holding the Keycloak-synced profile plus `groups` (jsonb) and `totalFunds`. It has two subclasses: `PhysicalUser` (real Keycloak-backed players) and `BotUser`. A `BotUser` carries a `Persona` — the one remaining hand-written enum (`domain.enumeration`, persisted with `@Enumerated(EnumType.STRING)`), whose constants pair a display name with a free-text play-style `instructions` string. Bots are added via the `sendBotConnected` WebSocket action (`CreateBotConnectionDTO`).

### Persistence

PostgreSQL with **Liquibase** migrations (`src/main/resources/liquibase/master.xml`). Hibernate runs with `ddl-auto: validate` — schema changes MUST go through a Liquibase changelog, never auto-DDL. Domain entities extend `Auditable`.

### Deterministic game testing

`FixedScenario` + `FixedScenarioShuffler` + `FixedScenarioTexasDealerService` replay a known deck order so tests are reproducible. Scenarios are CSV files in `src/test/resources` (e.g. `texas-holdem-3player-scenarios.csv`). The active profile picks the real `DefaultRandomShuffler` vs. the fixed one based on `APP_USE_FIXED_SCENARIO`.

## Conventions

- **ErrorProne** runs during compilation (`-Xplugin:ErrorProne`) — compile failures may originate from it, not just javac.
- Lombok is the codegen annotation processor (ErrorProne, above, is the other). DTO↔entity conversion lives in hand-written `@Component` mappers under `mapper/`.
- Spring profiles: `local` (default, DEBUG logging) and `cloud` (INFO). Config in `application.yml`.
- Spring beans configured for game logic are often **prototype/parameterized** beans fetched via `context.getBean(Type.class, args)` (e.g. `TexasGameThread`, turn services) — they are not singletons.
