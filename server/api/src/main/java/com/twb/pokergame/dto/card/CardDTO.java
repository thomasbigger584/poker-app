package com.twb.pokergame.dto.card;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardDTO {
    private int suit;
    private int rank;
}
