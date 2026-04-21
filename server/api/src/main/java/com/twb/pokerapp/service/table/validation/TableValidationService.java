package com.twb.pokerapp.service.table.validation;

import com.twb.pokerapp.dto.table.CreateTableDTO;
import com.twb.pokerapp.web.exception.ValidationException;

public abstract class TableValidationService {

    public void validate(CreateTableDTO dto) {
        validatePlayerCounts(dto);
        validateBuyIn(dto);

        onValidate(dto);
    }

    private void validatePlayerCounts(CreateTableDTO dto) {
        var minPlayers = dto.getGameType().getMinPlayerCount();
        if (dto.getMinPlayers() < minPlayers) {
            throw new ValidationException("Min Players should be at least " + minPlayers);
        }
        var maxPlayers = dto.getGameType().getMaxPlayerCount();
        if (dto.getMaxPlayers() > maxPlayers) {
            throw new ValidationException("Max Players should be at least " + maxPlayers);
        }
        if (dto.getMinPlayers() > dto.getMaxPlayers()) {
            throw new ValidationException("Min Players should be smaller or equaled to Max Players");
        }
    }

    private void validateBuyIn(CreateTableDTO dto) {
        if (dto.getMinBuyin().compareTo(dto.getMaxBuyin()) > 0) {
            throw new ValidationException("Min Buy-In should be smaller or equaled to Max Players");
        }
    }

    protected abstract void onValidate(CreateTableDTO dto);
}
