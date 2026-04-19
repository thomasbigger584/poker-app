package com.twb.pokerapp.dto.table;

import com.twb.pokerapp.domain.enumeration.GameType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TableDTO {
    private UUID id;
    private String name;
    private GameType gameType;
    private Double speedMultiplier;
    private Integer totalRounds;
    private Integer minPlayers;
    private Integer maxPlayers;
    private BigDecimal minBuyin;
    private BigDecimal maxBuyin;
}
