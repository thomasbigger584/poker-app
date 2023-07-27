package com.twb.pokergame.dto.round;

import com.twb.pokergame.domain.enumeration.RoundState;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class RoundDTO {
    private UUID id;
    private RoundState roundState;
}
