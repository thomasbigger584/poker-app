package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.BettingRoundRefund;
import com.twb.pokerapp.dto.bettinground.BettingRoundRefundDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PlayerSessionMapper.class})
public interface BettingRoundRefundMapper {

    BettingRoundRefundDTO modelToDto(BettingRoundRefund model);
}
