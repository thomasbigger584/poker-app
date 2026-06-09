package com.twb.pokerapp.testutils.http;

import com.google.protobuf.Message;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.proto.GameType;
import com.twb.pokerapp.proto.TableDTO;
import com.twb.pokerapp.testutils.game.params.scenario.ScenarioParams;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RequiredArgsConstructor
public class RestClient {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
    private static final int EXPOSED_PORT = 8081;
    private static final String API_BASE_URL = "http://localhost:%d".formatted(EXPOSED_PORT);
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PROTOBUF_MEDIA_TYPE = "application/x-protobuf";
    private static final Map<Keycloak, RestClient> INSTANCES = new HashMap<>();
    private final Keycloak keycloak;

    public static synchronized RestClient getInstance(Keycloak keycloak) {
        if (INSTANCES.containsKey(keycloak)) {
            return INSTANCES.get(keycloak);
        }
        var instance = new RestClient(keycloak);
        INSTANCES.put(keycloak, instance);
        return instance;
    }

    public TableDTO createTable(ScenarioParams params) throws Exception {
        var createDto = CreateTableDTO.newBuilder()
                .setName(UUID.randomUUID().toString())
                .setGameType(GameType.GAME_TYPE_TEXAS_HOLDEM)
                .setSpeedMultiplier(params.getSpeedMultiplier())
                .setTotalRounds(params.getTotalRounds())
                .setMinPlayers(params.getScenarioPlayers().size())
                .setMaxPlayers(6)
                .setMinBuyin(params.getMinBuyIn().toPlainString())
                .setMaxBuyin(BigDecimal.valueOf(10_000).toPlainString())
                .build();

        var createResponse = post(TableDTO.class, createDto, "/poker-table");
        assertEquals(HttpStatus.CREATED.value(), createResponse.httpResponse().statusCode());
        return createResponse.resultBody();
    }

    public <ResultBody> ApiHttpResponse<ResultBody> post(Class<ResultBody> resultClass,
                                                         Message requestBody, String endpoint) throws Exception {
        var bodyPublisher = (requestBody == null)
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofByteArray(requestBody.toByteArray());
        var request = HttpRequest.newBuilder()
                .header(HttpHeaders.CONTENT_TYPE, PROTOBUF_MEDIA_TYPE)
                .header(HttpHeaders.ACCEPT, PROTOBUF_MEDIA_TYPE)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .POST(bodyPublisher)
                .build();
        return executeRequest(resultClass, request);
    }

    public <ResultBody> ApiHttpResponse<ResultBody> get(Class<ResultBody> resultClass, String endpoint) throws Exception {
        var request = HttpRequest.newBuilder()
                .header(HttpHeaders.ACCEPT, PROTOBUF_MEDIA_TYPE)
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .GET().build();
        return executeRequest(resultClass, request);
    }

    public ApiHttpResponse<?> delete(String endpoint) throws Exception {
        var request = HttpRequest.newBuilder()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + getAccessToken())
                .uri(URI.create(API_BASE_URL + endpoint))
                .DELETE().build();
        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
        return new ApiHttpResponse<>(response, null);
    }

    private String getAccessToken() {
        var tokenManager = keycloak.tokenManager();
        var accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }

    private <ResultBody> ApiHttpResponse<ResultBody> executeRequest(Class<ResultBody> resultClass, HttpRequest request) throws Exception {
        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofByteArray());
        assert HttpStatus.valueOf(response.statusCode()).is2xxSuccessful() : "status code should be successful: " + response.statusCode();
        var result = parse(resultClass, response.body());
        return new ApiHttpResponse<>(response, result);
    }

    /** Parse a binary protobuf body into the generated message type via its static {@code parseFrom(byte[])}. */
    @SuppressWarnings("unchecked")
    private <ResultBody> ResultBody parse(Class<ResultBody> resultClass, byte[] body) throws Exception {
        var parseFrom = resultClass.getMethod("parseFrom", byte[].class);
        return (ResultBody) parseFrom.invoke(null, (Object) body);
    }

    public record ApiHttpResponse<ResultBody>(HttpResponse<byte[]> httpResponse, ResultBody resultBody) {
    }
}
