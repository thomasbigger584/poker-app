package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.RoundPot;
import com.twb.pokerapp.proto.RoundPotDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RoundPotMapper {
    private final PlayerSessionMapper playerSessionMapper;

    public RoundPotDTO modelToDto(RoundPot model) {
        if (model == null) {
            return null;
        }
        var builder = RoundPotDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setPotAmount(ProtoConvert.money(model.getPotAmount()));
        if (model.getPotIndex() != null) {
            builder.setPotIndex(model.getPotIndex());
        }
        if (model.getEligiblePlayers() != null) {
            model.getEligiblePlayers()
                    .forEach(player -> builder.addEligiblePlayers(playerSessionMapper.modelToDto(player)));
        }
        return builder.build();
    }
}
