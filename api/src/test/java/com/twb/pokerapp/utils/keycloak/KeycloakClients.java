package com.twb.pokerapp.utils.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import java.util.HashMap;

public class KeycloakClients extends HashMap<String, Keycloak> {
    private static final String KEYCLOAK_APP_CLIENT_ID = "poker-game-api-client";
    private static final String KEYCLOAK_ADMIN_REALM = "poker-app";
    public static final String KEYCLOAK_ADMIN_USERNAME = "admin";
    public static final String KEYCLOAK_ADMIN_PASSWORD = "admin";
    public static final String KEYCLOAK_VIEWER_USERNAME = "viewer1";
    private static final String USER_USERNAME_FORMAT = "user%d";
    private static final String KEYCLOAK_USER_PASSWORD = "password";
    private static final int KEYCLOAK_USER_COUNT = 6;

    public KeycloakClients(String serverUrl) {
        put(KEYCLOAK_ADMIN_USERNAME, createKeycloakClient(serverUrl, KEYCLOAK_ADMIN_USERNAME, KEYCLOAK_ADMIN_PASSWORD));
        put(KEYCLOAK_VIEWER_USERNAME, createKeycloakClient(serverUrl, KEYCLOAK_VIEWER_USERNAME, KEYCLOAK_USER_PASSWORD));
        for (int index = 0; index < KEYCLOAK_USER_COUNT; index++) {
            String username = String.format(USER_USERNAME_FORMAT, index + 1);
            put(username, createKeycloakClient(serverUrl, username, KEYCLOAK_USER_PASSWORD));
        }
    }

    private Keycloak createKeycloakClient(String serverUrl,
                                          String username, String password) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .clientId(KEYCLOAK_APP_CLIENT_ID)
                .realm(KEYCLOAK_ADMIN_REALM)
                .username(username)
                .password(password)
                .build();
    }

    public Keycloak getAdminKeycloak() {
        return get(KEYCLOAK_ADMIN_USERNAME);
    }

    public Keycloak getViewerKeycloak() {
        return get(KEYCLOAK_VIEWER_USERNAME);
    }
}
