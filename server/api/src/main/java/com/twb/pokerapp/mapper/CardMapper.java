package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.Card;
import com.twb.pokerapp.dto.card.CardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(source = "rankType.rankChar", target = "rankChar")
    @Mapping(source = "suitType.suitChar", target = "suitChar")
    CardDTO modelToDto(Card model);
}
