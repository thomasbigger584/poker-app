package com.twb.pokergame.dto.pokertable;

import com.twb.pokergame.domain.enumeration.GameType;
import lombok.Data;

import java.util.UUID;


@Data
public class PokerTableDTO {
    private UUID id;
    private String name;
    private GameType gameType;
}
