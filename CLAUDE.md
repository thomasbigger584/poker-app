# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository layout

Two independent modules under one repo:

- `server/` — Spring Boot backend (`server/api`, Java 21, Maven) plus one Docker Compose file per piece of infrastructure (`keycloak`, `postgres`, `rabbitmq`, `nginx`, `tailscale`). The root `server/docker-compose.yml` wires them all together; every service shares the Tailscale container's network namespace (`network_mode: service:tailscale`).
- `client/android/` — native Android app (Java/Kotlin, Gradle), package `com.twb.pokerapp`, minSdk 24 / targetSdk 35.

Backend and Android share DTO shapes and the WebSocket message protocol by hand (not generated) — when you change a server DTO or `ServerMessageType`, update the mirror under `client/android/.../data/model` and `data/websocket`.

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

`GameType` (enum in `domain/enumeration`) is the strategy factory: `TEXAS_HOLDEM` and `BLACKJACK` each resolve their own `GameThread`, `GamePlayerActionService`, `GamePlayerTurnService`, and `TableValidationService` beans from the `ApplicationContext`. To add a game variant, add an enum constant and implement those four beans under `service/game/thread/impl/<variant>`.

Player turns are coordinated with a `CountDownLatch` wrapped in `PlayerTurnLatchDTO`: the game thread blocks waiting for a turn; an inbound player action (on a different "caller" thread, marked `@CallerThread`) calls `onPostPlayerAction`, which counts the latch down. Server-pushed messages are sent **after** the DB transaction commits via `TransactionUtil.afterCommit(...)`.

There are two transaction templates: `writeTx` and `readTx` (the latter `@Qualifier("readTx")`). Use the matching one for reads vs. writes inside game logic.

### Hand evaluation — native JNI

`RankEvaluator` loads a native shared library (`evaluator.so`) via `System.load(System.getenv("EVALUATOR_SO_PATH"))` and calls `getRankNative(...)`. The `.so` is compiled in the `Dockerfile` from `src/main/cpp/PokerEvaluator.cpp` + the cloned [HenryRLee/PokerHandEvaluator](https://github.com/HenryRLee/PokerHandEvaluator) library. `HandTypeEvaluator` is the separate pure-Java path that classifies hand *type* (flush, straight, etc.). Hand ranking only works when `EVALUATOR_SO_PATH` points at a built `.so` (set in the Docker images).

### Real-time messaging — STOMP over WebSocket via RabbitMQ

- Clients subscribe to `/app/loops.{tableId}` — `TableWebSocketController.userSubscribed` returns initial game state.
- Broadcasts go out on `/topic/loops.{tableId}`, relayed through RabbitMQ (STOMP relay).
- Inbound client messages target `/app/pokerTable.{tableId}.<action>`: `sendPlayerAction`, `sendChatMessage`, `sendDisconnectPlayer` (see `TableWebSocketController`). The `sendBotConnected` action exists only on the `create-bot-functionality` branch.
- Server→client messages are `ServerMessageDTO` tagged by `ServerMessageType`, with payload DTOs under `web/websocket/message/server/payload`. `ServerMessageFactory` builds them; `MessageDispatcher` sends them.

### Auth & users

Keycloak owns identity (OAuth2 resource server; the API validates JWTs). Keycloak user events arrive over RabbitMQ and are handled by `KeycloakRabbitMqConsumer` / `UserRabbitMqConsumer` to sync the local `AppUser` table.

`AppUser` is a single concrete `@Entity` (table `app_user`) holding the Keycloak-synced profile plus `groups` (jsonb) and `totalFunds`. There is one user type on `master`.

> **Branch note:** bot players are a work-in-progress on the `create-bot-functionality` branch, where `AppUser` becomes abstract (`InheritanceType.JOINED`) with `PhysicalUser`/`BotUser` subclasses, a `Persona` entity (name + free-text `instructions` play style) seeded by `PersonaService`, and a `sendBotConnected` WebSocket action (`CreateBotConnectionDTO`). None of that exists on `master` — don't assume those types are present unless you're on that branch.

### Persistence

PostgreSQL with **Liquibase** migrations (`src/main/resources/liquibase/master.xml`). Hibernate runs with `ddl-auto: validate` — schema changes MUST go through a Liquibase changelog, never auto-DDL. Domain entities extend `Auditable`.

### Deterministic game testing

`FixedScenario` + `FixedScenarioShuffler` + `FixedScenarioTexasDealerService` replay a known deck order so tests are reproducible. Scenarios are CSV files in `src/test/resources` (e.g. `texas-holdem-3player-scenarios.csv`). The active profile picks the real `DefaultRandomShuffler` vs. the fixed one based on `APP_USE_FIXED_SCENARIO`.

## Conventions

- **ErrorProne** runs during compilation (`-Xplugin:ErrorProne`) — compile failures may originate from it, not just javac.
- Lombok + MapStruct (annotation processors). DTO↔entity conversion lives in `mapper/`.
- Spring profiles: `local` (default, DEBUG logging) and `cloud` (INFO). Config in `application.yml`.
- Spring beans configured for game logic are often **prototype/parameterized** beans fetched via `context.getBean(Type.class, args)` (e.g. `TexasGameThread`, turn services) — they are not singletons.
