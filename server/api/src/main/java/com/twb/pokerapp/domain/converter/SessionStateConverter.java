package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.SessionState;
import jakarta.persistence.Converter;

@Converter
public class SessionStateConverter extends ProtoEnumStringConverter<SessionState> {
    public SessionStateConverter() {
        super(SessionState.getDescriptor(), SessionState::forNumber);
    }
}
