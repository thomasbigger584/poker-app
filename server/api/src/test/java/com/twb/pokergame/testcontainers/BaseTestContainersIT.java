package com.twb.pokergame.testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokergame.configuration.KeycloakConfiguration;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

public abstract class BaseTestContainersIT {
    protected static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
    private static final Logger logger = LoggerFactory.getLogger("TEST");
    private static final String DOCKER_COMPOSE_LOCATION = "src/test/resources/";
    private static final String TEST_DOCKER_COMPOSE_YML = "test-docker-compose.yml";
    private static final String EXPOSED_SERVICE = "api";
    private static final int EXPOSED_PORT = 8081;
    protected static final String API_BASE_URL = String.format("http://localhost:%d", EXPOSED_PORT);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final String BEARER_PREFIX = "Bearer ";
    private static DockerComposeContainer<?> dockerComposeContainer;
    private static Keycloak keycloak;

    @BeforeAll
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void beforeAll() {
        File file = new File(DOCKER_COMPOSE_LOCATION + TEST_DOCKER_COMPOSE_YML);
        dockerComposeContainer = new DockerComposeContainer(file)
                .withExposedService(EXPOSED_SERVICE, EXPOSED_PORT)
                .withLogConsumer(EXPOSED_SERVICE, new Slf4jLogConsumer(logger).withPrefix(EXPOSED_SERVICE));
        dockerComposeContainer.start();
        keycloak = getKeycloak();
    }

    @AfterAll
    public static void afterAll() {
        dockerComposeContainer.stop();
    }

    protected static <ResultBody, RequestBody> ApiHttpResponse<ResultBody> post(Class<ResultBody> resultClass,
                                                                                RequestBody requestBody, String endpoint) throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return executeRequest(resultClass, request);
    }

    protected static <ResultBody> ApiHttpResponse<ResultBody> get(Class<ResultBody> resultClass, String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .GET().build();
        return executeRequest(resultClass, request);
    }

    protected static ApiHttpResponse<?> delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .DELETE().build();
        return executeRequest(request);
    }

    private static <ResultBody> ApiHttpResponse<ResultBody> executeRequest(Class<ResultBody> resultClass, HttpRequest request) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        ResultBody result = OBJECT_MAPPER.readValue(response.body(), resultClass);
        return new ApiHttpResponse<>(response, result);
    }

    private static ApiHttpResponse<?> executeRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiHttpResponse<>(response, Void.class);
    }

    protected static String getAccessToken() {
        TokenManager tokenManager = keycloak.tokenManager();
        AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }

    @SuppressWarnings("unchecked")
    private static Keycloak getKeycloak() {
        Map<String, Object> props = getProps();
        Map<String, Object> keycloakProps = (Map<String, Object>) props.get("keycloak");

        KeycloakConfiguration configuration = new KeycloakConfiguration();
        configuration.setServerUrl((String) keycloakProps.get("server-url"));
        configuration.setRealm((String) keycloakProps.get("realm"));
        configuration.setClientId((String) keycloakProps.get("client-id"));
        configuration.setUsername((String) keycloakProps.get("username"));
        configuration.setPassword((String) keycloakProps.get("password"));
        configuration.setAdminGroupId((String) keycloakProps.get("admin-group-id"));
        configuration.setUserGroupId((String) keycloakProps.get("user-group-id"));

        Client client = configuration.resteasyClient();
        return configuration.keycloak(client);
    }

    private static Map<String, Object> getProps() {
        Yaml yaml = new Yaml();
        return yaml.load(BaseTestContainersIT.class
                .getClassLoader()
                .getResourceAsStream("application.yml"));
    }

    @Getter
    @RequiredArgsConstructor
    protected static class ApiHttpResponse<ResultBody> {
        private final HttpResponse<String> httpResponse;
        private final ResultBody resultBody;
    }
}
