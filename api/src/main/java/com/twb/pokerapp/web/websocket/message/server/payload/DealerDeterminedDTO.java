package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

@Data
public class DealerDeterminedDTO {
    private PlayerSessionDTO playerSession;
}
