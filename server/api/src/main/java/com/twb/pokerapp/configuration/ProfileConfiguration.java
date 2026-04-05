package com.twb.pokerapp.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProfileConfiguration {
    public static final String TEST_PROFILE = "test";
    public static final String LOCAL_PROFILE = "local";
    public static final String CLOUD_PROFILE = "cloud";

    private final Environment environment;

    @PostConstruct
    public void init() {
        if (hasLocalProfile() && hasCloudProfile()) {
            throw new RuntimeException("Cannot set both local and cloud profiles");
        }
        if (hasCloudProfile() && hasTestProfile()) {
            throw new RuntimeException("Cannot set both cloud and test profiles");
        }
    }

    public boolean hasTestProfile() {
        var activeProfiles = environment.getActiveProfiles();
        return Arrays.asList(activeProfiles).contains(TEST_PROFILE);
    }

    public boolean hasLocalProfile() {
        var activeProfiles = environment.getActiveProfiles();
        return Arrays.asList(activeProfiles).contains(LOCAL_PROFILE);
    }

    public boolean hasCloudProfile() {
        var activeProfiles = environment.getActiveProfiles();
        return Arrays.asList(activeProfiles).contains(CLOUD_PROFILE);
    }
}
