package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.card.CardDTO;
import lombok.Data;

@Data
public class DealCommunityCardDTO {
    private CardDTO card;
}
