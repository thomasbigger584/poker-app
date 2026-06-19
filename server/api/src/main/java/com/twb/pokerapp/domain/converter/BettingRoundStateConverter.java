package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.BettingRoundState;
import jakarta.persistence.Converter;

@Converter
public class BettingRoundStateConverter extends ProtoEnumStringConverter<BettingRoundState> {
    public BettingRoundStateConverter() {
        super(BettingRoundState.getDescriptor(), BettingRoundState::forNumber);
    }
}
