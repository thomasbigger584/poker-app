package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.roundwinner.RoundWinnerDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoundFinishedDTO {
    private List<RoundWinnerDTO> winners = new ArrayList<>();
}
