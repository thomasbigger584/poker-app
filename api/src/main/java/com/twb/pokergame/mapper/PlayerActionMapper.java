package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.PlayerAction;
import com.twb.pokergame.dto.playeraction.PlayerActionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {PlayerSessionMapper.class, RoundMapper.class})
public interface PlayerActionMapper {
    PlayerActionDTO modelToDto(PlayerAction model);
}
