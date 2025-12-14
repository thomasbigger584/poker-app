package com.twb.pokerapp.dto.table;

import lombok.Data;

@Data
public class AvailableTableDTO {
    private TableDTO table;
    private int playersConnected;
}
