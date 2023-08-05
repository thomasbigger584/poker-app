package com.twb.pokergame.configuration;

import jakarta.ws.rs.client.Client;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.GroupsResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "keycloak.server-url")
public class KeycloakConfiguration {
    @Value("${keycloak.server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.username}")
    private String username;

    @Value("${keycloak.password}")
    private String password;

    @Value("${keycloak.admin-group-id}")
    private String adminGroupId;

    @Value("${keycloak.user-group-id}")
    private String userGroupId;

    @Bean
    public Client resteasyClient() {
        return new ResteasyClientBuilderImpl()
                .connectionPoolSize(10)
                .build();
    }

    @Bean
    public Keycloak keycloak(Client client) {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .username(username)
                .password(password)
                .resteasyClient(client)
                .build();
    }

    @Bean
    public RealmResource realmResource(Keycloak keycloak) {
        return keycloak.realm(realm);
    }

    @Bean
    public UsersResource usersResource(RealmResource realmResource) {
        return realmResource.users();
    }

    @Bean
    public GroupsResource groupsResource(RealmResource realmResource) {
        return realmResource.groups();
    }

    @Bean(name = "userGroupResource")
    public GroupResource userGroupResource(GroupsResource groupsResource) {
        return groupsResource.group(userGroupId);
    }

    @Bean(name = "adminGroupResource")
    public GroupResource adminGroupResource(GroupsResource groupsResource) {
        return groupsResource.group(adminGroupId);
    }

}
