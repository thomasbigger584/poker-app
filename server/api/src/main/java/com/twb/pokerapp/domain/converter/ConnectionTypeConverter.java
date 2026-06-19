package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.ConnectionType;
import jakarta.persistence.Converter;

@Converter
public class ConnectionTypeConverter extends ProtoEnumStringConverter<ConnectionType> {
    public ConnectionTypeConverter() {
        super(ConnectionType.getDescriptor(), ConnectionType::forNumber);
    }
}
