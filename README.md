# Poker App

![logo.png](logo.png)

## Usage

Add the following entries into your `/etc/hosts` file:
```text
127.0.0.1 	keycloak.pokerapp.local
127.0.0.1 	api.pokerapp.local
```

In your terminal use docker-compose to build and run the set of services
```shell
docker-compose up --build
```

Navigate to the following exposed services in your browser

| Service  | DNS Name                |
|----------|-------------------------|
| Keycloak | keycloak.pokerapp.local |
| API      | api.pokerapp.local      |

## Architecture

![PokerApp-Architecture.png](drawio%2FPokerApp-Architecture.png)

#### Consumer Mobile App

- The mobile application designed for regular users to interact with the games. It will connect to keycloak for authentication and then once logged in can access tables and connect to a table via websocket, and take part in the game 

#### Admin App

- An angular/react application which is used for Administrators to create new tables and view users etc.

#### Nginx

- A reverse proxy used to forward requests from the outside towards their respective internal services. Will also be used to handle SSL, and later down the line, handle load balancing. 

#### Keycloak

- A service used to handle users, roles, authentication and Single Sign-On (SSO). Any events that happen in Keycloak will get published onto a Rabbit MQ queue to be consumed by the java application to handle user signup. 

#### Poker Backend

- A Java Spring Boot application to handle all application business logic. 
- It will subscribe to the RabbitMQ keycloak topic to get user related events. 
- It will expose the REST API to handle creating tables and game management.
- It will expose a websocket for clients to connect to while playing the game to recieve updates in real time for updating their UI

#### Rabbit MQ

- A message broker used to publish events within the application. Currently used as the backing technology for websockets and also for keycloak to backend app user events. Can be used for any other eventing later.

#### PostgresQL (PSQL)

- The database of choice for storing both the keycloak persistence data and backend app persistence data.
