package com.twb.pokerapp.configuration.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthConverter.class);
    private static final String RESOURCE_ACCESS = "resource_access";
    private static final String ROLES = "roles";
    private static final String AUTHORITY_PREFIX = "ROLE_";

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter;
    private final JwtAuthConverterProperties properties;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        List<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toList());

        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
        setUserDetails(authToken);
        return authToken;
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (properties.getPrincipalAttribute() != null) {
            claimName = properties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> extractResourceRoles(Jwt jwt) {
        if (!jwt.hasClaim(RESOURCE_ACCESS)) {
            return Collections.emptyList();
        }
        Map<String, Object> resourceAccess = jwt.getClaim(RESOURCE_ACCESS);
        if (resourceAccess == null) {
            return Collections.emptyList();
        }
        if (!resourceAccess.containsKey(properties.getResourceId())) {
            return Collections.emptyList();
        }
        Map<String, Object> resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId());
        if (!resource.containsKey("roles")) {
            return Collections.emptyList();
        }
        Collection<String> resourceRoles = (Collection<String>) resource.get(ROLES);
        if (resourceRoles == null) {
            return Collections.emptyList();
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority(AUTHORITY_PREFIX + role))
                .collect(Collectors.toList());
    }

    private void setUserDetails(JwtAuthenticationToken authToken) {
        try {
            Optional<AppUser> userOpt = userRepository.findByUsername(authToken.getName());
            if (userOpt.isPresent()) {
                authToken.setDetails(objectMapper.writeValueAsString(userOpt.get()));
                authToken.setAuthenticated(true);
            }
        } catch (Exception e) {
            logger.warn("Failed to retrieve user to populate the authentication token details", e);
        }
    }
}
