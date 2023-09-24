package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.PokerTable;
import com.twb.pokergame.dto.pokertable.CreateTableDTO;
import com.twb.pokergame.dto.pokertable.TableDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableDTO modelToDto(PokerTable model);

    PokerTable createDtoToModel(CreateTableDTO dto);
}
