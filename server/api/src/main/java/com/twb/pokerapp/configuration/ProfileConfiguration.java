package com.twb.pokerapp.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class ProfileConfiguration {
    public static final String LOCAL_PROFILE = "local";
    public static final String DIGITALOCEAN_PROFILE = "digitalocean";

    private final Environment environment;

    @PostConstruct
    public void init() {
        if (hasLocalProfile() && hasDigitalOceanProfile()) {
            throw new RuntimeException("Cannot set both local and digitalocean profiles");
        }
    }

    public boolean hasLocalProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.asList(activeProfiles).contains(LOCAL_PROFILE);
    }

    public boolean hasDigitalOceanProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        return Arrays.asList(activeProfiles).contains(DIGITALOCEAN_PROFILE);
    }
}
