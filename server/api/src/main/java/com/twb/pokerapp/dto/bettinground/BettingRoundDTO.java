package com.twb.pokerapp.dto.bettinground;

import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import lombok.Data;

import java.util.UUID;

@Data
public class BettingRoundDTO {
    private UUID id;
    private BettingRoundState state;
    private Double pot;
}
