# Poker App

[![API CI](https://github.com/thomasbigger584/poker-app/actions/workflows/api.yml/badge.svg)](https://github.com/thomasbigger584/poker-app/actions/workflows/api.yml)
[![Android Release](https://github.com/thomasbigger584/poker-app/actions/workflows/android-release.yml/badge.svg)](https://github.com/thomasbigger584/poker-app/actions/workflows/android-release.yml)

![Logo](images/logo.png)

A comprehensive, real-time multiplayer poker application featuring a robust backend, a native Android client, and a secure, scalable architecture.

---

## Download APK

You can download the latest pre-built Android APK from our [**GitHub Releases page**](https://github.com/thomasbigger584/poker-app/releases).

> **Warning:** The application is currently in a test phase and under active development. To use the downloaded APK without modification it requires connecting to a specific tailnet for now. You may encounter bugs or incomplete features. Feedback and bug reports are highly appreciated!

---

## Table of Contents

- [Download APK](#download-apk)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Server Setup](#server-setup)
  - [Client Setup](#client-setup)
- [Testing](#testing)
  - [Running Backend Tests](#running-backend-tests)
  - [Running Blackbox Tests (IntelliJ)](#running-blackbox-tests-intellij)
- [Contributing](#contributing)
- [License](#license)

---

## Architecture

The application is designed with a modern, microservices-oriented architecture to ensure scalability, security, and maintainability.

![PokerApp-Architecture](images/PokerApp-Architecture.png)

### Core Components

-   **Poker Backend (Java/Spring Boot):** The central service handling all business logic, game management, and real-time communication via WebSockets.
-   **Consumer Mobile App (Android):** The native Android application for users to play the game. It authenticates with Keycloak and communicates with the backend in real-time.
-   **Keycloak:** Manages user identity, authentication (SSO), and authorization. It integrates with the backend via RabbitMQ for event-driven user management.
-   **Nginx:** Acts as a reverse proxy, directing incoming traffic to the appropriate internal services.
-   **RabbitMQ:** A message broker for asynchronous communication, primarily used for Keycloak user events and as the backing transport for WebSockets.
-   **PostgreSQL:** The primary relational database for both the application backend and Keycloak persistence.
-   **Tailscale:** Provides a secure virtual private network, assigning domain names to services for easy and secure inter-service communication and testing.
-   **Admin & CLI Apps:** Administrative and command-line tools for management and testing purposes.

---

## Features

-   **Real-time Multiplayer:** Play poker with others in real-time with updates delivered via WebSockets.
-   **Secure Authentication:** Robust user management and SSO handled by Keycloak.
-   **Containerized Deployment:** All backend services are containerized with Docker for consistent and easy setup.
-   **CI/CD:** Automated build and test pipelines for both the API and the Android client using GitHub Actions.
-   **Comprehensive Test Suite:** Includes unit, integration, and blackbox tests to ensure reliability.

---

## Technology Stack

-   **Backend:** Java, Spring Boot, Maven
-   **Frontend:** Android (Kotlin/Java)
-   **Database:** PostgreSQL
-   **Authentication:** Keycloak
-   **Messaging:** RabbitMQ
-   **Networking:** Nginx, Tailscale
-   **Containerization:** Docker, Docker Compose

---

## Getting Started

Follow these instructions to get the project running on your local machine for development and testing purposes.

### Prerequisites

-   [Docker](https://www.docker.com/get-started) and Docker Compose with buildx plugin and BuildKit enabled
-   [Java 21](https://www.oracle.com/java/technologies/downloads/) or newer
-   [Android Studio](https://developer.android.com/studio) (for the client)
-   A [Tailscale](https://tailscale.com/download) account and an Auth Key

### Server Setup

1.  **Configure Tailscale:**
    -   Create a reusable, non-ephemeral Tailscale Auth Key from the [admin console](https://login.tailscale.com/admin/settings/keys).
    -   Create a file at `server/api/env/.secrets.env`.
    -   Add your auth key to the file:
        ```env
        TS_AUTHKEY=your_tailscale_auth_key
        ```

2.  **Launch Backend Services:**
    -   Navigate to the `server/api` directory and run the following command to build and start all services.
        ```shell
        cd server/api
        docker compose up --build
        ```

### Client Setup

1.  **Open the Project:**
    -   Launch Android Studio and open the `client/android` directory as a project.

2.  **Run the App:**
    -   Once the project has synced, you can run the app on an emulator or a physical device.
    -   For multiplayer testing, consider using [Genymotion](https://www.genymotion.com/) to easily manage and run multiple virtual devices.

---

## Testing

### Running Backend Tests

You can run the complete test suite using the provided Maven wrapper.

-   From the `server/api` directory, execute the `run-tests` profile:
    ```shell
    ./mvnw clean verify -P run-tests
    ```
    *(If you encounter a permission error, run `chmod +x ./mvnw` first.)*

### Running Blackbox Tests (IntelliJ)

When running integration tests like `TexasGameIT` directly within IntelliJ IDEA:

1.  **Prerequisite:** Ensure Docker is running.
2.  **Maven Goal:** Configure your test run configuration to execute the `process-test-resources` Maven goal before the test runs. This copies necessary configuration files (like the Keycloak realm) into the test classpath.
3.  **Run Configuration:** An example run configuration is included in the `.idea/runConfigurations` directory when you open `server/api` in IntelliJ.
4.  **Delegate to Maven:** Alternatively, you can delegate the test execution to Maven within IntelliJ's settings, which will handle this automatically.

---

## Contributing

Contributions are welcome! Please feel free to submit a pull request or open an issue.

---

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
