# poker-game-backend

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

