package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.BettingRoundRefund;
import com.twb.pokerapp.proto.BettingRoundRefundDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BettingRoundRefundMapper {
    private final PlayerSessionMapper playerSessionMapper;

    public BettingRoundRefundDTO modelToDto(BettingRoundRefund model) {
        if (model == null) {
            return null;
        }
        var builder = BettingRoundRefundDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setAmount(ProtoConvert.money(model.getAmount()));
        if (model.getPlayerSession() != null) {
            builder.setPlayerSession(playerSessionMapper.modelToDto(model.getPlayerSession()));
        }
        return builder.build();
    }
}
