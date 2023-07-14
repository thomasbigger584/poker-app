package com.twb.pokergame.dto.pokertableuser;

import lombok.Data;

import java.util.UUID;


@Data
public class PokerTableUserDTO {
    private UUID id;
    private double funds = 0d;
    private int position;
}
