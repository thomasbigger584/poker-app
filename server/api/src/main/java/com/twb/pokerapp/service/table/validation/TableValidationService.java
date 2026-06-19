package com.twb.pokerapp.service.table.validation;

import com.twb.pokerapp.mapper.ProtoConvert;
import com.twb.pokerapp.proto.CreateTableDTO;
import com.twb.pokerapp.service.game.GameStrategies;
import com.twb.pokerapp.web.exception.ValidationException;

import java.math.BigDecimal;

public abstract class TableValidationService {

    public void validate(CreateTableDTO dto) {
        validateRequiredFields(dto);
        validatePlayerCounts(dto);
        validateBuyIn(dto);

        onValidate(dto);
    }

    /**
     * Required/positive field checks that previously lived as {@code @NotNull / @NotBlank / @Positive}
     * bean-validation annotations on the request DTO (proto messages can't carry them).
     */
    private void validateRequiredFields(CreateTableDTO dto) {
        if (dto.getName().isBlank()) {
            throw new ValidationException("name", "Table Name is required");
        }
        if (dto.getGameTypeValue() <= 0) {
            throw new ValidationException("gameType", "Game Type is required");
        }
        if (dto.getMinPlayers() <= 0) {
            throw new ValidationException("minPlayers", "Min Players should be a positive number");
        }
        if (dto.getMaxPlayers() <= 0) {
            throw new ValidationException("maxPlayers", "Max Players should be a positive number");
        }
        requirePositive(minBuyin(dto), "minBuyin", "Min Buy-In");
        requirePositive(maxBuyin(dto), "maxBuyin", "Max Buy-In");
    }

    private void validatePlayerCounts(CreateTableDTO dto) {
        var gameType = dto.getGameType();
        var minPlayers = GameStrategies.minPlayers(gameType);
        if (dto.getMinPlayers() < minPlayers) {
            throw new ValidationException("minPlayers", "Min Players should be at least " + minPlayers);
        }
        var maxPlayers = GameStrategies.maxPlayers(gameType);
        if (dto.getMaxPlayers() > maxPlayers) {
            throw new ValidationException("maxPlayers", "Max Players should be at least " + maxPlayers);
        }
        if (dto.getMinPlayers() > dto.getMaxPlayers()) {
            throw new ValidationException("minPlayers", "Min Players should be smaller or equaled to Max Players");
        }
    }

    private void validateBuyIn(CreateTableDTO dto) {
        if (minBuyin(dto).compareTo(maxBuyin(dto)) > 0) {
            throw new ValidationException("minBuyin", "Min Buy-In should be smaller or equaled to Max Buy-In");
        }
    }

    private BigDecimal minBuyin(CreateTableDTO dto) {
        return ProtoConvert.bigDecimal(dto.getMinBuyin());
    }

    private BigDecimal maxBuyin(CreateTableDTO dto) {
        return ProtoConvert.bigDecimal(dto.getMaxBuyin());
    }

    private void requirePositive(BigDecimal value, String field, String label) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException(field, label + " should be a positive number");
        }
    }

    protected abstract void onValidate(CreateTableDTO dto);
}
