package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.dto.bettinground.BettingRoundDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { })
public interface BettingRoundMapper {

    BettingRoundDTO modelToDto(BettingRound model);
}
