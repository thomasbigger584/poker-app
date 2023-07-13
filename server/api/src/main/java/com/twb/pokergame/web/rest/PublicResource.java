package com.twb.pokergame.web.rest;

import com.twb.pokergame.web.websocket.WebSocketController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicResource {
    private static final Logger logger = LoggerFactory.getLogger(PublicResource.class);

    private final WebSocketController controller;


    @GetMapping("/send-message")
    public ResponseEntity<Void> sendMessage() throws Exception {
        controller.sendMessage();

        return ResponseEntity.ok().build();
    }
}
