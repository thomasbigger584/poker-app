package com.twb.pokerapp.dto.handwinner;

import com.twb.pokerapp.dto.hand.HandDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.dto.round.RoundDTO;
import lombok.Data;

import java.util.UUID;

@Data
public class HandWinnerDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private RoundDTO round;
    private HandDTO hand;
    private Double amount;
}
