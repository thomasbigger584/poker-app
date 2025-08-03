package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.card.CardDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

@Data
public class DealPlayerCardDTO {
    private PlayerSessionDTO playerSession;
    private CardDTO card;
}
