package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.util.List;

@Data
public class PlayerSubscribedDTO {
    private List<PlayerSessionDTO> playerSessions;

    /**
     * In-progress hand state so a (re)subscribing client resumes exactly where the hand left off.
     * Null when no hand is currently being played.
     */
    private RoundStateDTO roundState;
}
