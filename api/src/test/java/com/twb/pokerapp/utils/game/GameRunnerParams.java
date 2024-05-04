package com.twb.pokerapp.utils.game;

import com.twb.pokerapp.dto.pokertable.TableDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GameRunnerParams {
    private int numberOfRounds;
    private GameLatches latches;
    private TableDTO table;
}


