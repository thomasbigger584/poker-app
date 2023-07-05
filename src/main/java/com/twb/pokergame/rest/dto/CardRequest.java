package com.twb.pokergame.rest.dto;

import lombok.Data;

import java.util.List;

@Data
public class CardRequest {
    private int rank;
    private int suit;
}
