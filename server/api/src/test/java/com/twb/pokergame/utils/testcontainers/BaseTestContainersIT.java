package com.twb.pokergame.utils.testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokergame.utils.keycloak.KeycloakHelper;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

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
        keycloak = KeycloakHelper.getKeycloak("admin", "admin");
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
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + KeycloakHelper.getAccessToken(keycloak))
                .uri(URI.create(API_BASE_URL + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return executeRequest(resultClass, request);
    }

    protected static <ResultBody> ApiHttpResponse<ResultBody> get(Class<ResultBody> resultClass, String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + KeycloakHelper.getAccessToken(keycloak))
                .uri(URI.create(API_BASE_URL + endpoint))
                .GET().build();
        return executeRequest(resultClass, request);
    }

    protected static ApiHttpResponse<?> delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + KeycloakHelper.getAccessToken(keycloak))
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

    protected record ApiHttpResponse<ResultBody>(HttpResponse<String> httpResponse, ResultBody resultBody) {
    }
}