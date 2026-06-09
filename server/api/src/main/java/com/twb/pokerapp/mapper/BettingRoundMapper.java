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
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setType(ProtoConvert.toProto(model.getType()))
                .setState(ProtoConvert.toProto(model.getState()));
        if (model.getBettingRoundRefunds() != null) {
            model.getBettingRoundRefunds()
                    .forEach(refund -> builder.addBettingRoundRefunds(bettingRoundRefundMapper.modelToDto(refund)));
        }
        return builder.build();
    }
}
