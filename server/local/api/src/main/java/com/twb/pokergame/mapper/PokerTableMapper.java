package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.dto.pokertable.PokerTableDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PokerTableMapper {

    PokerTableDTO modelToDto(PokerTable model);
}
