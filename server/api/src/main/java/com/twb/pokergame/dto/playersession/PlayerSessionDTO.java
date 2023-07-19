package com.twb.pokergame.dto.playersession;

import com.twb.pokergame.dto.appuser.AppUserDTO;
import com.twb.pokergame.dto.pokertable.TableDTO;
import lombok.Data;

import java.util.UUID;


@Data
public class PlayerSessionDTO {
    private UUID id;
    private AppUserDTO user;
    private TableDTO pokerTable;
    private Integer position;
    private Boolean dealer;
    private Double funds;
}
