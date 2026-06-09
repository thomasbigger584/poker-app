package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.PlayerAction;
import com.twb.pokerapp.proto.PlayerActionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlayerActionMapper {
    private final PlayerSessionMapper playerSessionMapper;
    private final BettingRoundMapper bettingRoundMapper;

    public PlayerActionDTO modelToDto(PlayerAction model) {
        if (model == null) {
            return null;
        }
        var builder = PlayerActionDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setActionType(ProtoConvert.toProto(model.getActionType()))
                .setAmount(ProtoConvert.money(model.getAmount()));
        if (model.getPlayerSession() != null) {
            builder.setPlayerSession(playerSessionMapper.modelToDto(model.getPlayerSession()));
        }
        if (model.getBettingRound() != null) {
            builder.setBettingRound(bettingRoundMapper.modelToDto(model.getBettingRound()));
        }
        return builder.build();
    }
}
