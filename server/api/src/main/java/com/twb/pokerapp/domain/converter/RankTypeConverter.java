package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.RankType;
import jakarta.persistence.Converter;

@Converter
public class RankTypeConverter extends ProtoEnumStringConverter<RankType> {
    public RankTypeConverter() {
        super(RankType.getDescriptor(), RankType::forNumber);
    }
}
