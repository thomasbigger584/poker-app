package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.GameType;
import jakarta.persistence.Converter;

@Converter
public class GameTypeConverter extends ProtoEnumStringConverter<GameType> {
    public GameTypeConverter() {
        super(GameType.getDescriptor(), GameType::forNumber);
    }
}
