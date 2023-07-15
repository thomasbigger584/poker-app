package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.PlayerSession;
import com.twb.pokergame.dto.playersession.PlayerSessionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, TableMapper.class, RoundMapper.class})
public interface PlayerSessionMapper {

    PlayerSessionDTO modelToDto(PlayerSession model);
}
