package com.twb.pokerapp.dto.bettinground;

import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class BettingRoundRefundDTO {
    private UUID id;
    private PlayerSessionDTO playerSession;
    private Double amount;
    private List<BettingRoundRefundDTO> bettingRoundRefunds = new ArrayList<>();
}
