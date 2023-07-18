package com.twb.pokergame.web.websocket.message.server.payload;

import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlayerSubscribedDTO {
    private List<PlayerSessionDTO> playerSessions;
}
