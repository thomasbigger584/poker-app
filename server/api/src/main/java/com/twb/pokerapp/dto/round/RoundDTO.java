package com.twb.pokerapp.dto.round;

import com.twb.pokerapp.domain.enumeration.RoundState;
import lombok.Data;

import java.util.UUID;

@Data
public class RoundDTO {
    private UUID id;
    private RoundState roundState;
    private Double pot;
}
