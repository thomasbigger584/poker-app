package com.twb.pokerapp.dto.pokertable;

import com.twb.pokerapp.domain.enumeration.GameType;
import lombok.Data;

@Data
public class CreateTableDTO {
    private String name;
    private GameType gameType;
}
