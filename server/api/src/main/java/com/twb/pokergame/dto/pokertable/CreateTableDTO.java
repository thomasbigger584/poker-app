package com.twb.pokergame.dto.pokertable;

import com.twb.pokergame.domain.enumeration.GameType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTableDTO {
    private String name;
    private GameType gameType;
}
