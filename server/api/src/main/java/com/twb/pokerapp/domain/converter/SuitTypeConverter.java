package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.SuitType;
import jakarta.persistence.Converter;

@Converter
public class SuitTypeConverter extends ProtoEnumStringConverter<SuitType> {
    public SuitTypeConverter() {
        super(SuitType.getDescriptor(), SuitType::forNumber);
    }
}
