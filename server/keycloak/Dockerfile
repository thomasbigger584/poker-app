FROM quay.io/keycloak/keycloak:26.4

ARG DOMAIN

ADD --chown=keycloak:keycloak https://github.com/ALMiG-Kompressoren-GmbH/tailcloakify/releases/download/v1.2.0/keycloak-theme-for-kc-22-to-25.jar /opt/keycloak/providers/tailcloakify-theme.jar

# Copy the realm template and perform substitutions
COPY poker-app-realm.json /tmp/poker-app-realm.json.template
RUN sed -i "s|__DOMAIN_PLACEHOLDER__|$DOMAIN|g" /tmp/poker-app-realm.json.template

# Move the processed realm file to the Keycloak import directory
RUN mv /tmp/poker-app-realm.json.template /opt/keycloak/data/import/poker-app-realm.json
