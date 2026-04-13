package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.dto.round.RoundDTO;
import com.twb.pokerapp.dto.roundpot.RoundPotDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BettingRoundUpdatedDTO {
    private RoundDTO round;
    private BettingRoundDTO bettingRound;
    private List<RoundPotDTO> roundPots = new ArrayList<>();
}
