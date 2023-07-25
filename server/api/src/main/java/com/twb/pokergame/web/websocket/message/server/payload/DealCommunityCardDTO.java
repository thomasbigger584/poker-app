package com.twb.pokergame.web.websocket.message.server.payload;

import com.twb.pokergame.domain.enumeration.CardType;
import com.twb.pokergame.dto.card.CardDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DealCommunityCardDTO {
    private CardType cardType;
    private CardDTO card;
}
