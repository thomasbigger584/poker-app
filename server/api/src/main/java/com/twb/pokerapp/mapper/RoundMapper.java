package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.Round;
import com.twb.pokerapp.proto.RoundDTO;
import org.springframework.stereotype.Component;

@Component
public class RoundMapper {

    public RoundDTO modelToDto(Round model) {
        if (model == null) {
            return null;
        }
        return RoundDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setRoundState(ProtoConvert.toProto(model.getRoundState()))
                .build();
    }
}
