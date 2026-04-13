package com.twb.pokerapp.service.game.thread.dto;

import com.twb.pokerapp.domain.PlayerSession;

public record ContributionDTO(PlayerSession player, Double amount,
                              boolean isFolded) implements Comparable<ContributionDTO> {
    @Override
    public int compareTo(ContributionDTO other) {
        return this.amount.compareTo(other.amount);
    }
}