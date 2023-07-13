package com.twb.pokergame.web.rest;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.repository.PokerTableRepository;
import com.twb.pokergame.web.websocket.dto.PokerAppWebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicResource {
    private final SimpMessageSendingOperations messagingTemplate;
    private final PokerTableRepository repository;

    @GetMapping("/send-message")
    public ResponseEntity<Void> sendMessage() {

        List<PokerTable> allPokerTables = repository.findAll();

        for (PokerTable pokerTable : allPokerTables) {
            PokerAppWebSocketMessage chatMessage = new PokerAppWebSocketMessage();
            chatMessage.setType("send-message-type");
            chatMessage.setSender("send-message-sender");
            chatMessage.setContent("send-message-content");

            messagingTemplate.convertAndSend(String.format("/ws.sendMessage/%s", pokerTable.getId()), chatMessage);
        }

        return ResponseEntity.ok().build();
    }
}
