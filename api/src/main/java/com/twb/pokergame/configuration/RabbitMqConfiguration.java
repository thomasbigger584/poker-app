package com.twb.pokergame.configuration;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@EnableRabbit
@Configuration
@Profile(ProfileConfiguration.DIGITALOCEAN_PROFILE)
public class RabbitMqConfiguration {
}
