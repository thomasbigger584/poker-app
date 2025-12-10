package com.twb.pokerapp.service.table.validation;

import com.twb.pokerapp.dto.pokertable.CreateTableDTO;

public abstract class TableValidationService {

    public void validate(CreateTableDTO dto) {
        validatePlayerCounts(dto);
        validateBuyIn(dto);

        onValidate(dto);
    }

    private void validatePlayerCounts(CreateTableDTO dto) {
        var minPlayers = dto.getGameType().getMinPlayerCount();
        if (dto.getMinPlayers() < minPlayers) {
            throw new RuntimeException("Min Players should be at least " + minPlayers);
        }
        var maxPlayers = dto.getGameType().getMaxPlayerCount();
        if (dto.getMaxPlayers() > maxPlayers) {
            throw new RuntimeException("Max Players should be at least " + maxPlayers);
        }
        if (dto.getMinPlayers() > dto.getMaxPlayers()) {
            throw new RuntimeException("Min Players should be smaller or equaled to Max Players");
        }
    }

    private void validateBuyIn(CreateTableDTO dto) {
        if (dto.getMinBuyin() > dto.getMaxBuyin()) {
            throw new RuntimeException("Min Buy-In should be smaller or equaled to Max Players");
        }
    }

    protected abstract void onValidate(CreateTableDTO dto);
}
