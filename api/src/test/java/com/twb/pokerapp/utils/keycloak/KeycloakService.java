package com.twb.pokerapp.utils.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

@RequiredArgsConstructor
public class KeycloakService {
    private final String serverUrl;
    private final Keycloak masterKeycloak;

    public Keycloak initializeAppRealm() {
        Keycloak appKeycloak = createAppKeycloak();

        createAdminUser(appKeycloak);

        return appKeycloak;
    }

    private Keycloak createAppKeycloak() {
        AccessTokenResponse accessToken = masterKeycloak
                .tokenManager().getAccessToken();
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("poker-app")
                .authorization(accessToken.getToken())
                .build();
    }

    private void createAdminUser(Keycloak appKeycloak) {
        UserRepresentation adminUser = new UserRepresentation();
        adminUser.setUsername("admin");
        adminUser.setFirstName("Administrator");
        adminUser.setLastName("Administrator");
        adminUser.setEmail("admin@pokerapp.com");
        adminUser.setEmailVerified(true);
        adminUser.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setTemporary(false);
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue("admin");

        adminUser.setCredentials(Collections.singletonList(credential));

        masterKeycloak.realm("poker-app").users().create(adminUser);
    }
}
