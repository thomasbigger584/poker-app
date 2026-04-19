package com.twb.pokerapp.dto.bettinground;

import com.twb.pokerapp.domain.enumeration.BettingRoundState;
import com.twb.pokerapp.domain.enumeration.BettingRoundType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class BettingRoundDTO {
    private UUID id;
    private BettingRoundType type;
    private BettingRoundState state;
    private List<BettingRoundRefundDTO> bettingRoundRefunds = new ArrayList<>();
}
