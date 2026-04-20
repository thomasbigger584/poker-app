package com.twb.pokerapp.testutils;

import com.twb.pokerapp.testutils.http.RestClient;
import com.twb.pokerapp.testutils.keycloak.KeycloakClients;
import com.twb.pokerapp.testutils.sql.SqlClient;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static java.lang.management.ManagementFactory.getRuntimeMXBean;

@Getter
public class TestEnvironment implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger("TEST");

    private static final String IMAGE_REPOSITORY = "com.twb.pokerapp";

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

    // RabbitMQ Constants
    private static final String RABBITMQ_IMAGE_NAME = IMAGE_REPOSITORY + "/rabbitmq";
    private static final String RABBITMQ_SERVICE = "rabbitmq";
    private static final int RABBITMQ_STOMP_PORT = 61613;
    private static final int RABBITMQ_AMQP_PORT = 5672;
    private static final int RABBITMQ_MGMT_PORT = 15672;

    // Spring Boot Property Keys
    private static final String SPRING_RABBITMQ_HOST_KEY = "SPRING_RABBITMQ_HOST";
    private static final String SPRING_RABBITMQ_STOMP_PORT_KEY = "SPRING_RABBITMQ_STOMP_PORT";
    private static final String SPRING_RABBITMQ_USERNAME_KEY = "SPRING_RABBITMQ_USERNAME";
    private static final String SPRING_RABBITMQ_PASSWORD_KEY = "SPRING_RABBITMQ_PASSWORD";

    // API Constants
    private static final String API_IMAGE_NAME = IMAGE_REPOSITORY + "/api";
    private static final String API_IMAGE_VERSION = "latest";
    private static final String API_SERVICE = "api";
    private static final String KEYCLOAK_SERVER_URL_INTERNAL_KEY = "KEYCLOAK_SERVER_URL_INTERNAL";
    private static final String KEYCLOAK_SERVER_URL_EXTERNAL_KEY = "KEYCLOAK_SERVER_URL_EXTERNAL";
    private static final int API_PORT = 8081;
    private static final int API_DEBUG_PORT = 5005;
    private static final String APP_USE_FIXED_SCENARIO_KEY = "APP_USE_FIXED_SCENARIO";
    private static final String ENV_POKERAPP_LOG_LEVEL = "POKERAPP_LOG_LEVEL";
    private static final String DEFAULT_POKERAPP_LOG_LEVEL = "DEBUG";
    private static final String POKERAPP_LOG_LEVEL_KEY = "LOGGING_LEVEL_COM_TWB_POKERAPP";

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

    private static final RabbitMQContainer RABBITMQ_CONTAINER =
            new RabbitMQContainer(DockerImageName.parse(RABBITMQ_IMAGE_NAME)
                    .asCompatibleSubstituteFor("rabbitmq"))
                    .withNetwork(NETWORK)
                    .withNetworkAliases(RABBITMQ_SERVICE)
                    .withAdminPassword("admin")
                    .withEnv("RABBITMQ_DEFAULT_USER", "admin")
                    .withEnv("RABBITMQ_DEFAULT_PASS", "admin")
                    .withPluginsEnabled("rabbitmq_stomp", "rabbitmq_management")
                    .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix(RABBITMQ_SERVICE));

    private static GenericContainer<?> API_CONTAINER;

    private KeycloakClients keycloakClients;
    private RestClient adminRestClient;
    private SqlClient sqlClient;

    static {
        DB_CONTAINER.setPortBindings(List.of(getPortBindingString(DB_PORT)));
        RABBITMQ_CONTAINER.setPortBindings(
                List.of(getPortBindingString(RABBITMQ_STOMP_PORT),
                        getPortBindingString(RABBITMQ_AMQP_PORT),
                        getPortBindingString(RABBITMQ_MGMT_PORT)));
    }

    public TestEnvironment start() {
        return start(false);
    }

    // todo: EnvironmentParams
    public TestEnvironment start(boolean useFixedScenario) {
        DB_CONTAINER.start();
        KEYCLOAK_CONTAINER.start();
        RABBITMQ_CONTAINER.start();

        //noinspection resource
        API_CONTAINER = new GenericContainer<>("%s:%s".formatted(API_IMAGE_NAME, API_IMAGE_VERSION))
                .withEnv(KEYCLOAK_SERVER_URL_INTERNAL_KEY, KEYCLOAK_HOSTNAME)
                .withEnv(KEYCLOAK_SERVER_URL_EXTERNAL_KEY, KEYCLOAK_HOSTNAME)
                .withEnv(SPRING_DATASOURCE_URL_KEY, DB_DATASOURCE_URL)

                // RabbitMQ Configuration
                .withEnv(SPRING_RABBITMQ_HOST_KEY, RABBITMQ_SERVICE)
                .withEnv(SPRING_RABBITMQ_STOMP_PORT_KEY, String.valueOf(RABBITMQ_STOMP_PORT))
                .withEnv(SPRING_RABBITMQ_USERNAME_KEY, "admin")
                .withEnv(SPRING_RABBITMQ_PASSWORD_KEY, "admin")
                .withEnv(APP_USE_FIXED_SCENARIO_KEY, String.valueOf(useFixedScenario))
                .withEnv(POKERAPP_LOG_LEVEL_KEY, System.getenv()
                        .getOrDefault(ENV_POKERAPP_LOG_LEVEL, DEFAULT_POKERAPP_LOG_LEVEL))
                .withExposedPorts(API_PORT)
                .withLogConsumer(new Slf4jLogConsumer(logger).withPrefix(API_SERVICE))
                .withNetwork(NETWORK)
                .withNetworkAliases(API_SERVICE)
                .dependsOn(KEYCLOAK_CONTAINER, DB_CONTAINER, RABBITMQ_CONTAINER);

        var isDebug = getRuntimeMXBean().getInputArguments().toString().contains("jdwp");
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

    public RestClient getUserRestClient(String username) {
        return RestClient.getInstance(keycloakClients.get(username));
    }

    public void afterEach() {
        sqlClient.truncate();
    }

    @Override
    public void close() {
        if (API_CONTAINER != null) API_CONTAINER.stop();
        if (KEYCLOAK_CONTAINER != null) KEYCLOAK_CONTAINER.stop();
        if (RABBITMQ_CONTAINER != null) RABBITMQ_CONTAINER.stop();
        if (DB_CONTAINER != null) DB_CONTAINER.stop();
    }

    // *****************************************************************************************
    // Helper Methods
    // *****************************************************************************************

    private static String getPortBindingString(int port) {
        return "%d:%d".formatted(port, port);
    }
}
