package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.CardType;
import jakarta.persistence.Converter;

@Converter
public class CardTypeConverter extends ProtoEnumStringConverter<CardType> {
    public CardTypeConverter() {
        super(CardType.getDescriptor(), CardType::forNumber);
    }
}
