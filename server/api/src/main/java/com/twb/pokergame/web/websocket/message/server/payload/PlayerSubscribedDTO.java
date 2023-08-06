package com.twb.pokergame.web.websocket.message.server.payload;

import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.util.List;

@Data
public class PlayerSubscribedDTO {
    private List<PlayerSessionDTO> playerSessions;
}
