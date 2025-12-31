package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.dto.round.RoundDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {RoundPotMapper.class})
public interface RoundMapper {

    RoundDTO modelToDto(Round model);
}
