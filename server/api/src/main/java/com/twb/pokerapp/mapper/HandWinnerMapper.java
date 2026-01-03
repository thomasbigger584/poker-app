package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.HandWinner;
import com.twb.pokerapp.dto.handwinner.HandWinnerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerSessionMapper.class, RoundMapper.class, HandMapper.class})
public interface HandWinnerMapper {

    HandWinnerDTO modelToDto(HandWinner model);
}
