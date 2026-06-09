package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.proto.CardDTO;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    public CardDTO modelToDto(Card model) {
        if (model == null) {
            return null;
        }
        var builder = CardDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setRankType(ProtoConvert.toProto(model.getRankType()))
                .setRankValue(model.getRankValue())
                .setSuitType(ProtoConvert.toProto(model.getSuitType()))
                .setCardType(ProtoConvert.toProto(model.getCardType()));
        if (model.getRankType() != null) {
            builder.setRankChar(ProtoConvert.ch(model.getRankType().getRankChar()));
        }
        if (model.getSuitType() != null) {
            builder.setSuitChar(ProtoConvert.ch(model.getSuitType().getSuitChar()));
        }
        return builder.build();
    }
}
