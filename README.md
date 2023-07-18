# Poker App

WORK IN PROGRESS

![logo.png](logo.png)

## Usage

In your terminal run the following command from the root directory to run the server locally:

```shell
./local_run.sh
```

To run the Android App, install Android Studio and open the folder `client/android` as a project. Once everything syncs and builds you can run it on your device.


| Service  | Domain Name          |
|----------|----------------------|
| Landing  | twbdev.site          |
| Keycloak | keycloak.twbdev.site |
| API      | localhost:8081       |

## Architecture

![PokerApp-Architecture.png](drawio%2FPokerApp-Architecture.png)

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

#### Nginx

- A reverse proxy used to forward requests from the outside towards their respective internal services.
- Will also be used to handle SSL, and later down the line, handle load balancing.

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
