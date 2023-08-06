package com.twb.pokergame.dto.pokertable;

import com.twb.pokergame.domain.enumeration.GameType;
import lombok.Data;

@Data
public class CreateTableDTO {
    private String name;
    private GameType gameType;
}
