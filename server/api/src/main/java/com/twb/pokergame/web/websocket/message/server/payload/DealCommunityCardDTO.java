package com.twb.pokergame.web.websocket.message.server.payload;

import com.twb.pokergame.dto.card.CardDTO;
import lombok.Data;

@Data
public class DealCommunityCardDTO {
    private CardDTO card;
}
