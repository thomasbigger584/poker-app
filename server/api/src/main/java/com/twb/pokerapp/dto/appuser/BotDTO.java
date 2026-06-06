package com.twb.pokerapp.dto.appuser;

import lombok.Data;

import java.util.UUID;

/**
 * A selectable bot player. Exposed so clients can list the fixed seeded bots and connect one to a
 * table via the {@code sendBotConnected} websocket action.
 */
@Data
public class BotDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String personaName;
    private String personaInstructions;
}
