package com.twb.pokergame.configuration;

import com.twb.pokergame.configuration.jwt.JwtAuthConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static com.twb.pokergame.configuration.Constants.ADMIN;
import static com.twb.pokergame.configuration.Constants.USER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfiguration {
    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //todo: fix this
        http.authorizeHttpRequests()
                .requestMatchers("/public", "/public/**").permitAll()
                .requestMatchers("/poker-app-ws", "/poker-app-ws/**").permitAll()
                .requestMatchers("/poker-table", "/poker-table/**").permitAll()
                .requestMatchers("/admin/**", "/admin/**").hasRole(ADMIN)
                .anyRequest().hasAnyRole(ADMIN, USER);
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthConverter);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
