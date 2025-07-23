# Poker App

[![Android CI](https://github.com/thomasbigger584/poker-app/actions/workflows/android.yml/badge.svg)](https://github.com/thomasbigger584/poker-app/actions/workflows/android.yml)
[![API CI](https://github.com/thomasbigger584/poker-app/actions/workflows/api.yml/badge.svg)](https://github.com/thomasbigger584/poker-app/actions/workflows/api.yml)

![logo.png](images/logo.png)

## Server Usage

### API Usage

#### Secret Environment

- Create file in `api/env/.secrets.env` with these keys, along with provided secret information.
```
TS_AUTHKEY=
```
- This file is git ignored for security reasons.

#### Tailscale

- Create a Tailscale Auth Key: https://login.tailscale.com/admin/settings/keys
- Make it Reusable, and not ephemeral. Give it the highest expiration.
- Place the Auth Key in secret environment file under the key `TS_AUTHKEY`
- Remove any machine in the tailnet with name `poker-app`. Ensure once the application starts that no additional
  machines are created.

#### Starting Application

```shell
$ cd api
$ docker compose up --build
```

## Client Usage

### Android Studio

- To run the Android App, install Android Studio and open the folder `client/android` as a project.
- Once everything syncs and builds you can run it on your device.
- For testing multiplayer, install Genymotion and setup 2 virtual devices. Can then build and run to multiple devices
  from Android Studio.

## Architecture

![PokerApp-Architecture.png](images/PokerApp-Architecture.png)

#### Consumer Mobile App

- The mobile application designed for regular users to interact with the games.
- It will connect to keycloak for authentication and then once logged in can access tables and connect to a table via
  websocket, and take part in the game.
- Once it subscripts to a websocket, receiving updates from the server in realtime of any updates which happen on the
  table and update the UI accordingly.

#### Admin App

- An angular/react application which is used for Administrators to create new tables and view users etc.
- Can be very basic for now but I would want this in place to make configuration easier once the application is
  deployed.

#### CLI App

- A CLI application used primarily for test purpose to listen to websocket messages.
- The user will log in as a listener user, and will select a Table ID and receive all messages on that table.

#### Tailscale

- Tailscale is used to assign domain names to the services and have all the services on its own virtual private network.
- Useful for testing without exposing the services over the internet.

#### Nginx

- A reverse proxy used to forward requests from the outside towards their respective internal services.

#### Keycloak

- A service used to handle users, roles, authentication and Single Sign-On (SSO).
- Any events that happen in Keycloak will get published onto a Rabbit MQ queue to be consumed by the java application to
  handle user signup.

#### Poker Backend

- A Java Spring Boot application to handle all application business logic.
- It will subscribe to the RabbitMQ keycloak topic to get user related events.
- It will expose the REST API to handle creating tables and game management.
- It will expose a websocket for clients to connect to while playing the game to receive updates in real time for
  updating their UI.

#### Rabbit MQ

- A message broker used to publish events within the application.
- Currently used as the backing technology for websockets and also for keycloak to backend app user events.
- Can be used for any other eventing later.

#### PostgresQL (PSQL)

- The database of choice for storing both the keycloak persistence data and backend app persistence data.
