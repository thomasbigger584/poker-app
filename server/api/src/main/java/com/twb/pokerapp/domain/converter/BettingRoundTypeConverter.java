package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.BettingRoundType;
import jakarta.persistence.Converter;

@Converter
public class BettingRoundTypeConverter extends ProtoEnumStringConverter<BettingRoundType> {
    public BettingRoundTypeConverter() {
        super(BettingRoundType.getDescriptor(), BettingRoundType::forNumber);
    }
}
