package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.domain.enumeration.ActionType;
import com.twb.pokerapp.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PlayerTurnDTO {
    private PlayerSessionDTO playerSession;
    private BettingRoundDTO bettingRound;
    private ActionType[] nextActions;
    private BigDecimal amountToCall;
    private Long playerTurnWaitMs;
}
