package com.twb.pokerapp.service.game.thread.dto;

import com.twb.pokerapp.domain.PlayerSession;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ContributionDTO implements Comparable<ContributionDTO> {
    private final PlayerSession player;
    private final Double amount;
    private final boolean isFolded;

    @Override
    public int compareTo(ContributionDTO other) {
        return this.amount.compareTo(other.amount);
    }
}