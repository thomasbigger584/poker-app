package com.twb.pokerapp.dto.bettinground;

import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.BettingRoundType;
import lombok.Data;

import java.util.UUID;

@Data
public class BettingRoundDTO {
    private UUID id;
    private BettingRoundType type;
    private BettingRoundState state;
    private Double pot;
}
