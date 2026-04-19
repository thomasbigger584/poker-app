package com.twb.pokerapp.dto.roundwinner;

import com.twb.pokerapp.dto.hand.HandDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.dto.round.RoundDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RoundWinnerDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private RoundDTO round;
    private HandDTO hand;
    private BigDecimal amount;
}
