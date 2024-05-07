package com.twb.pokerapp.utils.keycloak;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

import java.util.HashMap;

public class KeycloakClients extends HashMap<String, Keycloak> {
    private static final String APP_CLIENT_ID = "poker-game-api-client";
    private static final String ADMIN_REALM = "poker-app";
    public static final String ADMIN_USERNAME = "admin";
    public static final String ADMIN_PASSWORD = "admin";
    public static final String VIEWER_USERNAME = "viewer1";
    private static final String USER_USERNAME_FORMAT = "user%d";
    private static final String USER_PASSWORD = "password";
    private static final int USER_COUNT = 6;

    public KeycloakClients(String serverUrl) {
        put(ADMIN_USERNAME, createKeycloakClient(serverUrl, ADMIN_USERNAME, ADMIN_PASSWORD));
        put(VIEWER_USERNAME, createKeycloakClient(serverUrl, VIEWER_USERNAME, USER_PASSWORD));
        for (int index = 0; index < USER_COUNT; index++) {
            String username = String.format(USER_USERNAME_FORMAT, index + 1);
            put(username, createKeycloakClient(serverUrl, username, USER_PASSWORD));
        }
    }

    private Keycloak createKeycloakClient(String serverUrl,
                                          String username, String password) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .clientId(APP_CLIENT_ID)
                .realm(ADMIN_REALM)
                .username(username)
                .password(password)
                .build();
    }

    public Keycloak getAdminKeycloak() {
        return get(ADMIN_USERNAME);
    }

    public Keycloak getViewerKeycloak() {
        return get(VIEWER_USERNAME);
    }
}
