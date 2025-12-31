package com.twb.pokerapp.dto.roundpot;

import lombok.Data;

import java.util.UUID;

@Data
public class RoundPotDTO {
    private UUID id;
    private Double potAmount;
    private Integer potIndex;
}
