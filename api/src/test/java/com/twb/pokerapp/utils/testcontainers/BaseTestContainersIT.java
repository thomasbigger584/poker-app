package com.twb.pokerapp.utils.testcontainers;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger("TEST");

    // Auth Constants
    private static final String AUTH_SERVICE = "keycloak";
    private static final String AUTH_ADMIN_USERNAME = "admin";
    private static final String AUTH_ADMIN_PASSWORD = "admin";

    // DB Constants
    private static final String DB_SERVICE = "postgres";
    private static final int DB_PORT = 5432;

    // API Constants
    private static final String API_SERVICE = "api";
    private static final int API_PORT = 8081;
    private static final int API_DEBUG_PORT = 5005;

    // Test Containers
    private static final Slf4jLogConsumer LOG_CONSUMER = new Slf4jLogConsumer(logger);
    private static final Network NETWORK = Network.newNetwork();
    private static final KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer()
            .withRealmImportFile("/keycloak-realm.json")
            .withAdminUsername(AUTH_ADMIN_USERNAME)
            .withAdminPassword(AUTH_ADMIN_PASSWORD)
            .withLogConsumer(LOG_CONSUMER.withPrefix(AUTH_SERVICE))
            .withNetwork(NETWORK)
            .withNetworkAliases(AUTH_SERVICE)
            .withNetworkMode("bridge");
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.1-alpine")
            .withUsername("root")
            .withPassword("password")
            .withDatabaseName("db")
            .withExposedPorts(DB_PORT)
            .withLogConsumer(LOG_CONSUMER.withPrefix(DB_SERVICE))
            .withNetwork(NETWORK)
            .withNetworkAliases(DB_SERVICE)
            .withNetworkMode("bridge")
            .dependsOn(KEYCLOAK_CONTAINER);
    private static final GenericContainer<?> API_CONTAINER = new GenericContainer<>("com.twb.pokerapp/api:latest")
            .withEnv("KEYCLOAK_SERVER_URL", "http://keycloak:8080")
            .withExposedPorts(API_PORT)
            .withLogConsumer(LOG_CONSUMER.withPrefix(API_SERVICE))
            .withNetwork(NETWORK)
            .withNetworkAliases(API_SERVICE)
            .withNetworkMode("bridge")
            .dependsOn(KEYCLOAK_CONTAINER, POSTGRESQL_CONTAINER);

    static {
        POSTGRESQL_CONTAINER.setPortBindings(
                List.of(String.format("%d:%d", DB_PORT, DB_PORT)));
        API_CONTAINER.setPortBindings(
                List.of(String.format("%d:%d", API_PORT, API_PORT),
                        String.format("%d:%d", API_DEBUG_PORT, API_DEBUG_PORT)));
    }

    protected static Keycloak keycloak;

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @BeforeAll
    public static void onBeforeAll() {
        KEYCLOAK_CONTAINER.start();
        keycloak = KEYCLOAK_CONTAINER.getKeycloakAdminClient();
    }

    @BeforeEach
    public void onBeforeEach() throws Throwable {
        POSTGRESQL_CONTAINER.start();
        API_CONTAINER.start();
        beforeEach();
    }

    @AfterEach
    public void onAfterEach() throws Throwable {
        API_CONTAINER.stop();
        POSTGRESQL_CONTAINER.stop();
        afterEach();
    }

    @AfterAll
    public static void onAfterAll() {
        KEYCLOAK_CONTAINER.stop();
    }

    // *****************************************************************************************
    // Overridable Methods
    // *****************************************************************************************

    protected void beforeEach() throws Throwable {
    }

    protected void afterEach() throws Throwable {
    }
}
