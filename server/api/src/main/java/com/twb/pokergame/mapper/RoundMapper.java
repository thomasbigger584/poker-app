package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.Round;
import com.twb.pokergame.dto.round.RoundDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CardMapper.class})
public interface RoundMapper {

    RoundDTO modelToDto(Round model);
}
