# Poker App

[![Android CI](https://github.com/thomasbigger584/poker-app/actions/workflows/android.yml/badge.svg)](https://github.com/thomasbigger584/poker-app/actions/workflows/android.yml)
[![API CI](https://github.com/thomasbigger584/poker-app/actions/workflows/api.yml/badge.svg)](https://github.com/thomasbigger584/poker-app/actions/workflows/api.yml)

![logo.png](images/logo.png)

## Server Usage

### Setup Bootstrap

Create a python virtual environment, activate it and install requirements

```shell
$ cd bootstrap
$ virtualenv -p python3 venv
$ source venv/bin/activate
$ pip install -r requirements.txt
```

Run the `run.py` script

```shell
python3 run.py
```

```
usage: run.py [-h] [--no-build] [--no-cache] [--no-run]

Bootstrap poker-app Server

options:
  -h, --help  show this help message and exit
  --no-build  Skip building Docker images
  --no-cache  Build without cache
  --no-run    Skip running Docker containers
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
