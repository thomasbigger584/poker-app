package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.BettingRound;
import com.twb.pokerapp.proto.BettingRoundDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BettingRoundMapper {
    private final BettingRoundRefundMapper bettingRoundRefundMapper;

    public BettingRoundDTO modelToDto(BettingRound model) {
        if (model == null) {
            return null;
        }
        var builder = BettingRoundDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()));
        if (model.getType() != null) {
            builder.setType(model.getType());
        }
        if (model.getState() != null) {
            builder.setState(model.getState());
        }
        if (model.getBettingRoundRefunds() != null) {
            model.getBettingRoundRefunds()
                    .forEach(refund -> builder.addBettingRoundRefunds(bettingRoundRefundMapper.modelToDto(refund)));
        }
        return builder.build();
    }
}
