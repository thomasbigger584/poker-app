package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.Hand;
import com.twb.pokergame.dto.hand.HandDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CardMapper.class,})
public interface HandMapper {
    @Mapping(source = "handType.value", target = "handTypeStr")
    HandDTO modelToDto(Hand model);
}
