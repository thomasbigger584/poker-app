package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.util.List;

@Data
public class PlayerSubscribedDTO {
    private List<PlayerSessionDTO> playerSessions;
}
