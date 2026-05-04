package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.TransactionHistory;
import com.twb.pokerapp.dto.transactionhistory.TransactionHistoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionHistoryMapper {

    TransactionHistoryDTO modelToDto(TransactionHistory model);
}
