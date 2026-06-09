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
        return TransactionHistoryDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(model.getId()))
                .setAmount(ProtoConvert.money(model.getAmount()))
                .setType(ProtoConvert.toProto(model.getType()))
                .setCreatedDateTime(ProtoConvert.dateTime(model.getCreatedDateTime()))
                .build();
    }
}
