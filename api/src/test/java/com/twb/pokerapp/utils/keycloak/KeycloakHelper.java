package com.twb.pokerapp.utils.keycloak;

import com.twb.pokerapp.configuration.KeycloakConfiguration;
import com.twb.pokerapp.utils.testcontainers.BaseTestContainersIT;
import jakarta.ws.rs.client.Client;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class KeycloakHelper {
    private static final String KEYCLOAK = "keycloak";
    private static final String SERVER_URL = "server-url";
    private static final String REALM = "realm";
    private static final String CLIENT_ID = "client-id";
    private static final String ADMIN_GROUP_ID = "admin-group-id";
    private static final String USER_GROUP_ID = "user-group-id";

    @SuppressWarnings("unchecked")
    public static Keycloak getKeycloak(String username, String password) {
        Map<String, Object> props = getProps();
        Map<String, Object> keycloakProps = (Map<String, Object>) props.get(KEYCLOAK);

        KeycloakConfiguration configuration = new KeycloakConfiguration();
        configuration.setServerUrl((String) keycloakProps.get(SERVER_URL));
        configuration.setRealm((String) keycloakProps.get(REALM));
        configuration.setClientId((String) keycloakProps.get(CLIENT_ID));
        configuration.setUsername(username);
        configuration.setPassword(password);
        configuration.setAdminGroupId((String) keycloakProps.get(ADMIN_GROUP_ID));
        configuration.setUserGroupId((String) keycloakProps.get(USER_GROUP_ID));

        Client client = configuration.resteasyClient();
        return configuration.keycloak(client);
    }

    private static Map<String, Object> getProps() {
        Yaml yaml = new Yaml();
        return yaml.load(BaseTestContainersIT.class
                .getClassLoader()
                .getResourceAsStream("application.yml"));
    }

    public static String getAccessToken(Keycloak keycloak) {
        TokenManager tokenManager = keycloak.tokenManager();
        AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }
}
