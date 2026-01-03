package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.handwinner.HandWinnerDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoundFinishedDTO {
    private List<HandWinnerDTO> winners = new ArrayList<>();
}
