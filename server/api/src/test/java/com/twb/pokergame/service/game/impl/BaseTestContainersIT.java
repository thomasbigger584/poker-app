package com.twb.pokergame.service.game.impl;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.io.File;

public abstract class BaseTestContainersIT {
    private static final Logger logger = LoggerFactory.getLogger("TEST");
    private static final String DOCKER_COMPOSE_LOCATION = "src/test/resources/";
    private static final String TEST_DOCKER_COMPOSE_YML = "test-docker-compose.yml";
    private static final String EXPOSED_SERVICE = "api";
    private static final int EXPOSED_PORT = 8081;
    private static DockerComposeContainer<?> dockerComposeContainer;

    @BeforeAll
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void beforeAll() {
        File file = new File(DOCKER_COMPOSE_LOCATION + TEST_DOCKER_COMPOSE_YML);
        dockerComposeContainer = new DockerComposeContainer(file)
                .withExposedService(EXPOSED_SERVICE, EXPOSED_PORT)
                .withLogConsumer(EXPOSED_SERVICE, new Slf4jLogConsumer(logger).withPrefix(EXPOSED_SERVICE));
        dockerComposeContainer.start();
    }

    @AfterAll
    public static void afterAll() {
        dockerComposeContainer.stop();
    }
}
