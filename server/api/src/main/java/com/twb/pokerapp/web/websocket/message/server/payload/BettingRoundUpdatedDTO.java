package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.dto.round.RoundDTO;
import lombok.Data;

@Data
public class BettingRoundUpdatedDTO {
    private RoundDTO round;
    private BettingRoundDTO bettingRound;
}
