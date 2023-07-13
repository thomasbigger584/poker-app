package com.twb.pokergame.web.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.twb.pokergame.web.websocket.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebSocketController {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketController.class);

    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper;

    /*
     * Generic message sent to the subscriber.
     * Using dot "." in the topic path because rabbitmq doesn't support forward-slash "/" as a separator
     */
    @MessageMapping("/ws.sendMessage") // send message endpoint
    @SendTo("/topic/loops") // clients subscription endpoint
    public WebSocketMessage sendMessage(@Payload WebSocketMessage message) {
        logger.info("WEBSOCKET (sendMessage) with {}", message);
        return message;
    }

    public void sendMessage() throws Exception {
        logger.info("sendMessage - start");

        WebSocketMessage message = new WebSocketMessage();
        message.setType("send-message-type");
        message.setSender("send-message-sender");
        message.setContent("send-message-content");

        template.convertAndSend("/topic/loops", objectMapper.writeValueAsString(message));

        logger.info("sendMessage - message sent");
    }













//
//    /*
//     * Specific handlers for message sent to the subscriber
//     */
//
//    @MessageMapping("/ws.newPlayer/{pokerTableId}")
//    @SendTo("/topic/poker-app-events.{pokerTableId}") // clients subscription endpoint
//    public PokerAppWebSocketMessage newUser(@DestinationVariable String pokerTableId,
//                                            @Payload PokerAppWebSocketMessage message,
//                                            SimpMessageHeaderAccessor headerAccessor) {
//        logger.info("WEBSOCKET (newUser) - response to table {} with {}", pokerTableId, message);
//
//        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
//        sessionAttributes.put("username", message.getSender());
//
//        return message;
//    }
}
