package com.twb.pokerapp.dto.bettinground;

import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BettingRoundRefundDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private BigDecimal amount;
}
