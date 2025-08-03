package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.dto.playeraction.PlayerActionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {PlayerSessionMapper.class, RoundMapper.class})
public interface PlayerActionMapper {
    PlayerActionDTO modelToDto(PlayerAction model);
}
