package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.RoundWinner;
import com.twb.pokerapp.proto.RoundWinnerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoundWinnerMapper {
    private final PlayerSessionMapper playerSessionMapper;
    private final RoundMapper roundMapper;
    private final HandMapper handMapper;

    public RoundWinnerDTO modelToDto(RoundWinner model) {
        if (model == null) {
            return null;
        }
        var builder = RoundWinnerDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setAmount(ProtoConvert.money(model.getAmount()));
        if (model.getPlayerSession() != null) {
            builder.setPlayerSession(playerSessionMapper.modelToDto(model.getPlayerSession()));
        }
        if (model.getRound() != null) {
            builder.setRound(roundMapper.modelToDto(model.getRound()));
        }
        if (model.getHand() != null) {
            builder.setHand(handMapper.modelToDto(model.getHand()));
        }
        return builder.build();
    }
}
