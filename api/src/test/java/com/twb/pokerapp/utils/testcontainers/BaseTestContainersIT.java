package com.twb.pokerapp.utils.testcontainers;

import com.twb.pokerapp.utils.keycloak.KeycloakService;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Arrays;
import java.util.List;

public abstract class BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger("TEST");

    // Keycloak Constants
    private static final String KEYCLOAK_SERVICE = "keycloak";
    private static final String KEYCLOAK_REALM_JSON_FILE_PATH = "/keycloak-realm.json";
    private static final String KEYCLOAK_ADMIN_USERNAME = "admin";
    private static final String KEYCLOAK_ADMIN_PASSWORD = "admin";
    private static final int KEYCLOAK_PORT = 8080;

    // DB Constants
    private static final String DB_SERVICE = "postgres";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final int DB_PORT = 5432;

    // API Constants
    private static final String API_SERVICE = "api";
    private static final int API_PORT = 8081;
    private static final int API_DEBUG_PORT = 5005;

    // Test Containers
    private static final Slf4jLogConsumer LOG_CONSUMER = new Slf4jLogConsumer(logger);
    private static final Network NETWORK = Network.newNetwork();
    private static final KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer()
            .withRealmImportFile(KEYCLOAK_REALM_JSON_FILE_PATH)
            .withAdminUsername(KEYCLOAK_ADMIN_USERNAME)
            .withAdminPassword(KEYCLOAK_ADMIN_PASSWORD)
            .withLogConsumer(LOG_CONSUMER.withPrefix(KEYCLOAK_SERVICE))
            .withNetwork(NETWORK)
            .withNetworkAliases(KEYCLOAK_SERVICE)
            .withNetworkMode("bridge");
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.1-alpine")
            .withUsername(DB_USERNAME)
            .withPassword(DB_PASSWORD)
            .withDatabaseName("db")
            .withExposedPorts(DB_PORT)
            .withLogConsumer(LOG_CONSUMER.withPrefix(DB_SERVICE))
            .withNetwork(NETWORK)
            .withNetworkAliases(DB_SERVICE)
            .withNetworkMode("bridge")
            .dependsOn(KEYCLOAK_CONTAINER);
    private static final GenericContainer<?> API_CONTAINER = new GenericContainer<>("com.twb.pokerapp/api:latest")
            .withEnv("KEYCLOAK_SERVER_URL", String.format("http://%s:%d", KEYCLOAK_SERVICE, KEYCLOAK_PORT))
            .withExposedPorts(API_PORT)
            .withLogConsumer(LOG_CONSUMER.withPrefix(API_SERVICE))
            .withNetwork(NETWORK)
            .withNetworkAliases(API_SERVICE)
            .withNetworkMode("bridge")
            .dependsOn(KEYCLOAK_CONTAINER, POSTGRESQL_CONTAINER);

    protected static Keycloak masterKeycloak;
    protected static Keycloak pokerAppKeycloak;

    static {
        POSTGRESQL_CONTAINER.setPortBindings(
                List.of(getPortBindingString(DB_PORT)));
        API_CONTAINER.setPortBindings(
                List.of(getPortBindingString(API_PORT),
                        getPortBindingString(API_DEBUG_PORT)));
    }

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    @BeforeAll
    public static void onBeforeAll() {
        KEYCLOAK_CONTAINER.start();
        masterKeycloak = KEYCLOAK_CONTAINER.getKeycloakAdminClient();
        KeycloakService keycloakService = new KeycloakService(KEYCLOAK_CONTAINER.getAuthServerUrl(), masterKeycloak);
        pokerAppKeycloak = keycloakService.initializeAppRealm();
        System.out.println("keycloakService = " + keycloakService);
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

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private static String getPortBindingString(int port) {
        return String.format("%d:%d", port, port);
    }
}
