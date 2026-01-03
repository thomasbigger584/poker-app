package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.RoundWinner;
import com.twb.pokerapp.dto.roundwinner.RoundWinnerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerSessionMapper.class, RoundMapper.class, HandMapper.class})
public interface RoundWinnerMapper {

    RoundWinnerDTO modelToDto(RoundWinner model);
}
