package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, TableMapper.class, HandMapper.class})
public interface PlayerSessionMapper {

    PlayerSessionDTO modelToDto(PlayerSession model);
}
