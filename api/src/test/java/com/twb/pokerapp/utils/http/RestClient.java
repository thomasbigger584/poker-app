package com.twb.pokerapp.utils.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
public class RestClient {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
    private static final int EXPOSED_PORT = 8081;
    private static final String API_BASE_URL = "http://localhost:%d".formatted(EXPOSED_PORT);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static RestClient instance;
    private final Keycloak keycloak;

    public static synchronized RestClient getInstance(Keycloak keycloak) {
        if (instance == null) {
            instance = new RestClient(keycloak);
        }
        return instance;
    }

    public <ResultBody, RequestBody> ApiHttpResponse<ResultBody> post(Class<ResultBody> resultClass, RequestBody requestBody, String endpoint) throws Exception {
        String json = OBJECT_MAPPER.writeValueAsString(requestBody);
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return executeRequest(resultClass, request);
    }

    public <ResultBody> ApiHttpResponse<ResultBody> get(Class<ResultBody> resultClass, String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .GET().build();
        return executeRequest(resultClass, request);
    }

    public ApiHttpResponse<?> delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .DELETE().build();
        return executeRequest(request);
    }

    private String getAccessToken() {
        TokenManager tokenManager = keycloak.tokenManager();
        AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }

    private <ResultBody> ApiHttpResponse<ResultBody> executeRequest(Class<ResultBody> resultClass, HttpRequest request) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        ResultBody result = OBJECT_MAPPER.readValue(response.body(), resultClass);
        return new ApiHttpResponse<>(response, result);
    }

    private ApiHttpResponse<?> executeRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiHttpResponse<>(response, Void.class);
    }

    public record ApiHttpResponse<ResultBody>(HttpResponse<String> httpResponse, ResultBody resultBody) {
    }
}
