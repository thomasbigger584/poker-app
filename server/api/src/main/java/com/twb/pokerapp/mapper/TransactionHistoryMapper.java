package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.TransactionHistory;
import com.twb.pokerapp.proto.TransactionHistoryDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionHistoryMapper {

    public TransactionHistoryDTO modelToDto(TransactionHistory model) {
        if (model == null) {
            return null;
        }
        var builder = TransactionHistoryDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setAmount(ProtoConvert.money(model.getAmount()))
                .setCreatedDateTime(ProtoConvert.dateTime(model.getCreatedDateTime()));
        if (model.getType() != null) {
            builder.setType(model.getType());
        }
        return builder.build();
    }
}
