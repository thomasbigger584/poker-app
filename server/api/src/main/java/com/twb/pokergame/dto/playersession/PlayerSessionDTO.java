package com.twb.pokergame.dto.playersession;

import com.twb.pokergame.dto.appuser.AppUserDTO;
import com.twb.pokergame.dto.pokertable.TableDTO;
import com.twb.pokergame.dto.round.RoundDTO;
import lombok.Data;

import java.util.UUID;


@Data
public class PlayerSessionDTO {
    private UUID id;
    private AppUserDTO user;
    private TableDTO pokerTable;
    private RoundDTO round;
    private Integer position;
    private Double funds;
}
