package com.twb.pokerapp.utils.testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokerapp.utils.keycloak.KeycloakHelper;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    protected static final String API_BASE_URL = "http://localhost:%d".formatted(EXPOSED_PORT);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";
    private static DockerComposeContainer<?> dockerComposeContainer;
    private static Keycloak keycloak;

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

    @BeforeEach
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void beforeEach() {
        File file = new File(DOCKER_COMPOSE_LOCATION + TEST_DOCKER_COMPOSE_YML);
        dockerComposeContainer = new DockerComposeContainer(file)
                .withExposedService(EXPOSED_SERVICE, EXPOSED_PORT)
                .withLogConsumer(EXPOSED_SERVICE, new Slf4jLogConsumer(logger).withPrefix(EXPOSED_SERVICE));
        dockerComposeContainer.start();
        keycloak = KeycloakHelper.getKeycloak(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    @AfterEach
    public void afterEach() {
        dockerComposeContainer.stop();
    }

    protected record ApiHttpResponse<ResultBody>(HttpResponse<String> httpResponse, ResultBody resultBody) {
    }
}
