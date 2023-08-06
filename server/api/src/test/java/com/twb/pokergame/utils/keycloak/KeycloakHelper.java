package com.twb.pokergame.utils.keycloak;

import com.twb.pokergame.configuration.KeycloakConfiguration;
import com.twb.pokergame.utils.testcontainers.BaseTestContainersIT;
import jakarta.ws.rs.client.Client;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class KeycloakHelper {

    @SuppressWarnings("unchecked")
    public static Keycloak getKeycloak(String username, String password) {
        Map<String, Object> props = getProps();
        Map<String, Object> keycloakProps = (Map<String, Object>) props.get("keycloak");

        KeycloakConfiguration configuration = new KeycloakConfiguration();
        configuration.setServerUrl((String) keycloakProps.get("server-url"));
        configuration.setRealm((String) keycloakProps.get("realm"));
        configuration.setClientId((String) keycloakProps.get("client-id"));
        configuration.setUsername(username);
        configuration.setPassword(password);
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

    public static String getAccessToken(Keycloak keycloak) {
        TokenManager tokenManager = keycloak.tokenManager();
        AccessTokenResponse accessTokenResponse = tokenManager.getAccessToken();
        return accessTokenResponse.getToken();
    }
}
