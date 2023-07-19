package com.twb.pokergame.web.websocket.message.server.payload;

import com.twb.pokergame.dto.card.CardDTO;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DealPlayerCardDTO {
    private PlayerSessionDTO playerSession;
    private CardDTO card;
}
