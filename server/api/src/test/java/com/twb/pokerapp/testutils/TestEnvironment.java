package com.twb.pokerapp.testutils;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.testutils.http.RestClient;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import com.twb.pokerapp.testutils.sql.SqlClient;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.List;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;

@Getter
public class TestEnvironment implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger("TEST");

    // Keycloak Constants
    private static final String KEYCLOAK_SERVICE = "keycloak";
    private static final int KEYCLOAK_PORT = 8080;
    private static final String KEYCLOAK_REALM_JSON = "poker-app-realm.json";
    private static final String KEYCLOAK_HOSTNAME_KEY = "KC_HOSTNAME";
    private static final String KEYCLOAK_HOSTNAME = "http://%s:%d".formatted(KEYCLOAK_SERVICE, KEYCLOAK_PORT);

    // DB Constants
    private static final String DB_IMAGE_NAME = "postgres";
    private static final String DB_IMAGE_VERSION = "13.1-alpine";
    private static final String DB_SERVICE = "postgres";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "password";
    private static final String DB_NAME = "db";
    private static final int DB_PORT = 5432;

    private static final String SPRING_DATASOURCE_URL_KEY = "SPRING_DATASOURCE_URL";
    private static final String DB_DATASOURCE_URL = "jdbc:postgresql://%s:%d/%s".formatted(DB_SERVICE, DB_PORT, DB_NAME);

    // API Constants
    private static final String API_IMAGE_NAME = "com.twb.pokerapp/api";
    private static final String API_IMAGE_VERSION = "latest";
    private static final String API_SERVICE = "api";
    private static final String KEYCLOAK_SERVER_URL_INTERNAL_KEY = "KEYCLOAK_SERVER_URL_INTERNAL";
    private static final String KEYCLOAK_SERVER_URL_EXTERNAL_KEY = "KEYCLOAK_SERVER_URL_EXTERNAL";
    private static final int API_PORT = 8081;
    private static final int API_DEBUG_PORT = 5005;
    private static final String APP_USE_FIXED_SCENARIO = "APP_USE_FIXED_SCENARIO";
    private static final String APP_SPEED_MULTIPLIER = "APP_SPEED_MULTIPLIER";

    // Test Containers
    private static final Network NETWORK = Network.newNetwork();
    private static final KeycloakContainer KEYCLOAK_CONTAINER =
            new KeycloakContainer()
                    .withRealmImportFile(KEYCLOAK_REALM_JSON)
                    .withAdminUsername(KeycloakClients.ADMIN_USERNAME)
                    .withAdminPassword(KeycloakClients.ADMIN_PASSWORD)
                    .withNetwork(NETWORK)
                    .withNetworkAliases(KEYCLOAK_SERVICE)
                    .withEnv(KEYCLOAK_HOSTNAME_KEY, KEYCLOAK_HOSTNAME)
                    .withVerboseOutput();
    private static final PostgreSQLContainer DB_CONTAINER =
            new PostgreSQLContainer("%s:%s".formatted(DB_IMAGE_NAME, DB_IMAGE_VERSION))
                    .withUsername(DB_USERNAME)
                    .withPassword(DB_PASSWORD)
                    .withDatabaseName(DB_NAME)
                    .withExposedPorts(DB_PORT)
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix(DB_SERVICE))
                    .withNetwork(NETWORK)
                    .withNetworkAliases(DB_SERVICE)
                    .dependsOn(KEYCLOAK_CONTAINER);

    private static GenericContainer<?> API_CONTAINER;

    private KeycloakClients keycloakClients;
    private RestClient adminRestClient;
    private SqlClient sqlClient;

    static {
        DB_CONTAINER.setPortBindings(
                List.of(getPortBindingString(DB_PORT)));
    }

    // *****************************************************************************************
    // Lifecycle Methods
    // *****************************************************************************************

    public TestEnvironment start() {
        return start(false, 1);
    }

    // todo: EnvironmentParams
    public TestEnvironment start(boolean useFixedScenario, int speedMultiplier) {
        DB_CONTAINER.start();
        KEYCLOAK_CONTAINER.start();
        //noinspection resource
        API_CONTAINER = new GenericContainer<>("%s:%s".formatted(API_IMAGE_NAME, API_IMAGE_VERSION))
                .withEnv(KEYCLOAK_SERVER_URL_INTERNAL_KEY, KEYCLOAK_HOSTNAME)
                .withEnv(KEYCLOAK_SERVER_URL_EXTERNAL_KEY, KEYCLOAK_HOSTNAME)
                .withEnv(SPRING_DATASOURCE_URL_KEY, DB_DATASOURCE_URL)
                .withEnv(APP_USE_FIXED_SCENARIO, String.valueOf(useFixedScenario))
                .withEnv(APP_SPEED_MULTIPLIER, String.valueOf(speedMultiplier))
                .withExposedPorts(API_PORT)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix(API_SERVICE))
                .withNetwork(NETWORK)
                .withNetworkAliases(API_SERVICE)
                .dependsOn(KEYCLOAK_CONTAINER, DB_CONTAINER);
        boolean isDebug = getRuntimeMXBean()
                .getInputArguments().toString().contains("jdwp");
        if (isDebug) {
            API_CONTAINER.setPortBindings(
                    List.of(getPortBindingString(API_PORT),
                            getPortBindingString(API_DEBUG_PORT)));
            API_CONTAINER.setCommand(
                    "java",
                    "-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:" + API_DEBUG_PORT,
                    "-Djava.security.egd=file:/dev/./urandom",
                    "-jar",
                    "api.jar"
            );
        } else {
            API_CONTAINER.setPortBindings(List.of(getPortBindingString(API_PORT)));
        }
        API_CONTAINER.start();
        keycloakClients = new KeycloakClients(KEYCLOAK_CONTAINER.getAuthServerUrl());
        adminRestClient = RestClient.getInstance(keycloakClients.getAdminKeycloak());
        sqlClient = new SqlClient(DB_CONTAINER);
        return this;
    }

    public void afterEach() {
        waitForSessionsToDisconnect();
        sqlClient.truncate();
    }

    @Override
    public void close() {
        API_CONTAINER.stop();
        KEYCLOAK_CONTAINER.stop();
        DB_CONTAINER.stop();
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private static String getPortBindingString(int port) {
        return "%d:%d".formatted(port, port);
    }

    private void waitForSessionsToDisconnect() {
        var timeout = System.currentTimeMillis() + 10000;
        while (System.currentTimeMillis() < timeout) {
            var sessions = sqlClient.getPlayerSessions();
            if (sessions.isEmpty()|| isAllDisconnected(sessions)) {
                return;
            }
            try {
                //noinspection BusyWait
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException("Failed to sleep waiting for sessions to disconnect", e);
            }
        }
    }

    private boolean isAllDisconnected(List<PlayerSession> sessions) {
        return sessions.stream().allMatch(playerSession -> SessionState.CONNECTED != playerSession.getSessionState());
    }
}
