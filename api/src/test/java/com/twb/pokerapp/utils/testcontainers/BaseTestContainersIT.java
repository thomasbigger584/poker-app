package com.twb.pokerapp.utils.testcontainers;

import com.twb.pokerapp.utils.keycloak.KeycloakHelper;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.File;

public abstract class BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger("TEST");

    // Docker Compose Constants
    private static final String DOCKER_COMPOSE_LOCATION = "src/test/resources/";
    private static final String TEST_DOCKER_COMPOSE_YML = "test-docker-compose.yml";
    private static final File DOCKER_COMPOSE_FILE = new File(DOCKER_COMPOSE_LOCATION + TEST_DOCKER_COMPOSE_YML);

    // DB Constants
    private static final String DB_SERVICE = "postgres";

    // API Constants
    private static final String API_SERVICE = "api";
    private static final int API_PORT = 8081;

    // Auth Constants
    private static final String AUTH_SERVICE = "auth";
    private static final String AUTH_ADMIN_USERNAME = "admin";
    private static final String AUTH_ADMIN_PASSWORD = "admin";

    // Test Containers
    private static final Slf4jLogConsumer LOG_CONSUMER = new Slf4jLogConsumer(logger);
    protected static final KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer()
            .withRealmImportFile("/keycloak-realm.json")
            .withAdminUsername(AUTH_ADMIN_USERNAME)
            .withAdminPassword(AUTH_ADMIN_PASSWORD)
            .withLogConsumer(LOG_CONSUMER.withPrefix(AUTH_SERVICE));

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static final DockerComposeContainer<?> DOCKER_COMPOSE_CONTAINER = new DockerComposeContainer(DOCKER_COMPOSE_FILE)
            .withLogConsumer(DB_SERVICE, LOG_CONSUMER.withPrefix(DB_SERVICE))
            .withLogConsumer(API_SERVICE, LOG_CONSUMER.withPrefix(API_SERVICE))
            .withExposedService(API_SERVICE, API_PORT);

    protected static Keycloak keycloak;

    @BeforeAll
    public static void onBeforeAll() {
        KEYCLOAK_CONTAINER.start();
        keycloak = KeycloakHelper.getKeycloak(AUTH_ADMIN_USERNAME, AUTH_ADMIN_PASSWORD);
    }

    @BeforeEach
    public void onBeforeEach() {
        DOCKER_COMPOSE_CONTAINER.start();
        beforeEach();
    }

    @AfterEach
    public void onAfterEach() {
        DOCKER_COMPOSE_CONTAINER.stop();
        afterEach();
    }

    @AfterAll
    public static void onAfterAll() {
        KEYCLOAK_CONTAINER.stop();
    }

    protected void beforeEach() {}
    protected void afterEach() {}
}
