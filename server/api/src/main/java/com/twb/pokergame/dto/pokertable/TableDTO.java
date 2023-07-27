package com.twb.pokergame.dto.pokertable;

import com.twb.pokergame.domain.enumeration.GameType;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TableDTO {
    private UUID id;
    private String name;
    private GameType gameType;
}
