package com.twb.pokergame.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @RabbitListener(queues = "app.keycloak.queue")
    public void receivedMessage(Object employee) {
        System.out.println("Received Message From RabbitMQ: " + employee);
    }
}
