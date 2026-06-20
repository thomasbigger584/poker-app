package com.twb.pokerapp.domain.converter;

import com.twb.pokerapp.proto.TransactionHistoryType;
import jakarta.persistence.Converter;

@Converter
public class TransactionHistoryTypeConverter extends ProtoEnumStringConverter<TransactionHistoryType> {
    public TransactionHistoryTypeConverter() {
        super(TransactionHistoryType.getDescriptor(), TransactionHistoryType::forNumber);
    }
}
