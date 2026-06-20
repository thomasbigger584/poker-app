package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.ActionType;
import jakarta.persistence.Converter;

@Converter
public class ActionTypeConverter extends ProtoEnumStringConverter<ActionType> {
    public ActionTypeConverter() {
        super(ActionType.getDescriptor(), ActionType::forNumber);
    }
}
