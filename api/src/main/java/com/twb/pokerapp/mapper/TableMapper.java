package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.PokerTable;
import com.twb.pokerapp.dto.pokertable.CreateTableDTO;
import com.twb.pokerapp.dto.pokertable.TableDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableDTO modelToDto(PokerTable model);

    PokerTable createDtoToModel(CreateTableDTO dto);
}
