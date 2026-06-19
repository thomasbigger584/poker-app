package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.HandType;
import jakarta.persistence.Converter;

@Converter
public class HandTypeConverter extends ProtoEnumStringConverter<HandType> {
    public HandTypeConverter() {
        super(HandType.getDescriptor(), HandType::forNumber);
    }
}
