package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.dto.round.RoundDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerActionMapper.class})
public interface BettingRoundMapper {

    BettingRoundDTO modelToDto(BettingRound model);
}
