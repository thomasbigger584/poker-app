package com.twb.pokerapp.data.websocket.message.server.payload;

import androidx.annotation.NonNull;

import com.twb.pokerapp.data.model.dto.roundwinner.RoundWinnerDTO;

import java.util.ArrayList;
import java.util.List;

public class RoundFinishedDTO {
    private List<RoundWinnerDTO> winners = new ArrayList<>();

    public List<RoundWinnerDTO> getWinners() {
        return winners;
    }

    public void setWinners(List<RoundWinnerDTO> winners) {
        this.winners = winners;
    }

    @NonNull
    @Override
    public String toString() {
        return "RoundFinishedDTO{" +
                "winners=" + winners +
                '}';
    }
}
