package com.twb.pokerapp.mapper;

import com.twb.pokerapp.domain.Hand;
import com.twb.pokerapp.dto.hand.HandDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CardMapper.class,})
public interface HandMapper {
    @Mapping(source = "handType.value", target = "handTypeStr")
    HandDTO modelToDto(Hand model);
}
