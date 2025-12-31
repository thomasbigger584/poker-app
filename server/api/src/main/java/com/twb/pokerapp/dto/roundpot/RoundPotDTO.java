package com.twb.pokerapp.dto.roundpot;

import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class RoundPotDTO {
    private UUID id;
    private Double potAmount;
    private Integer potIndex;
    private List<PlayerSessionDTO> eligiblePlayers = new ArrayList<>();
}
