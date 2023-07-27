package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.Card;
import com.twb.pokergame.dto.card.CardDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardDTO modelToDto(Card model);
}
