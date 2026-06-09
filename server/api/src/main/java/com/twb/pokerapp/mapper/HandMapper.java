package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.proto.HandDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HandMapper {
    private final CardMapper cardMapper;

    public HandDTO modelToDto(Hand model) {
        if (model == null) {
            return null;
        }
        var builder = HandDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setHandType(ProtoConvert.toProto(model.getHandType()));
        if (model.getHandType() != null) {
            builder.setHandTypeStr(ProtoConvert.text(model.getHandType().getValue()));
        }
        if (model.getCards() != null) {
            model.getCards().forEach(card -> builder.addCards(cardMapper.modelToDto(card)));
        }
        return builder.build();
    }
}
