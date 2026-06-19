package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.RoundState;
import jakarta.persistence.Converter;

@Converter
public class RoundStateConverter extends ProtoEnumStringConverter<RoundState> {
    public RoundStateConverter() {
        super(RoundState.getDescriptor(), RoundState::forNumber);
    }
}
