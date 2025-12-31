package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.RoundPot;
import com.twb.pokerapp.dto.roundpot.RoundPotDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoundPotMapper {

    RoundPotDTO modelToDto(RoundPot model);
}
