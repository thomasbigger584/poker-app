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
public class SecurityConfiguration {
    private final JwtAuthConverter jwtAuthConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //todo: fix this
        http.authorizeHttpRequests()

                // public endpoint
                .requestMatchers("/public", "/public/**").permitAll()

                // websocket endpoints
                .requestMatchers("/looping").hasAuthority(USER)
                .requestMatchers("/looping/**").permitAll()

                //todo: remove these, only added for ease of testing
                .requestMatchers("/poker-table/send-message").permitAll()
                .requestMatchers("/player-session", "/player-session/**").permitAll()
                .requestMatchers("/round", "/round/**").permitAll()

                //admin endpoints
                .requestMatchers("/admin", "/admin/**").hasRole(ADMIN)

                //catch-all
                .anyRequest().hasAnyRole(ADMIN, USER);
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthConverter);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
