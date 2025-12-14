package com.twb.pokerapp.dto.playersession;

import com.twb.pokerapp.domain.enumeration.ConnectionType;
import com.twb.pokerapp.domain.enumeration.SessionState;
import com.twb.pokerapp.dto.appuser.AppUserDTO;
import com.twb.pokerapp.dto.table.TableDTO;
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
    private SessionState sessionState;
    private ConnectionType connectionType;
}
