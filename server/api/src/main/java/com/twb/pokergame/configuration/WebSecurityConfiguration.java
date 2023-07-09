package com.twb.pokergame.configuration;

import com.twb.pokergame.configuration.jwt.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    private static final String ADMIN = "admin";
    private static final String USER = "user";

    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //todo: fix this
        http.authorizeHttpRequests()
                .requestMatchers("/public", "/public/**").permitAll()
                .requestMatchers("/poker-app-ws", "/poker-app-ws/**").permitAll()
                .requestMatchers("/admin/**", "/admin/**").hasRole(ADMIN)
                .anyRequest().hasAnyRole(ADMIN, USER);
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthConverter);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
