package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.PokerTableUser;
import com.twb.pokergame.dto.pokertableuser.PokerTableUserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PokerTableUserMapper {

    PokerTableUserDTO modelToDto(PokerTableUser model);
}
