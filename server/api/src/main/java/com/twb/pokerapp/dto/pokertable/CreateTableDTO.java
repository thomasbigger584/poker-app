package com.twb.pokerapp.dto.pokertable;

import com.twb.pokerapp.domain.enumeration.GameType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class CreateTableDTO {
    @NotNull(message = "Table Name is required")
    @NotBlank(message = "Table Name should not be blank")
    private String name;

    @NotNull(message = "Game Type is required")
    private GameType gameType;

    @NotNull(message = "Min Players is required")
    @Positive(message = "Min Players should be a positive number")
    private Integer minPlayers;

    @NotNull(message = "Max Players is required")
    @Positive(message = "Max Players should be a positive number")
    private Integer maxPlayers;
}
