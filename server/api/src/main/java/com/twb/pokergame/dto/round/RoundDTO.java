package com.twb.pokergame.dto.round;

import com.twb.pokergame.domain.enumeration.RoundState;
import lombok.Data;

import java.util.UUID;


@Data
public class RoundDTO {
    private UUID id;
    private RoundState roundState;
}
