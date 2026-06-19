package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.mapper.enumeration.Ranks;
import com.twb.pokerapp.mapper.enumeration.Suits;
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
                .setRankValue(model.getRankValue());
        if (model.getRankType() != null) {
            builder.setRankType(model.getRankType());
            builder.setRankChar(ProtoConvert.ch(Ranks.charOf(model.getRankType())));
        }
        if (model.getSuitType() != null) {
            builder.setSuitType(model.getSuitType());
            builder.setSuitChar(ProtoConvert.ch(Suits.charOf(model.getSuitType())));
        }
        if (model.getCardType() != null) {
            builder.setCardType(model.getCardType());
        }
        return builder.build();
    }
}
