package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.dto.card.CardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(source = "rankType.rankChar", target = "rankChar")
    @Mapping(source = "suitType.suitChar", target = "suitChar")
    CardDTO modelToDto(Card model);
}
