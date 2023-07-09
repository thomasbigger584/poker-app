package com.twb.pokergame.mapper;

import com.twb.pokergame.domain.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "groups", target = "groups", qualifiedByName = "mapGroups")
    User representationToModel(UserRepresentation representation);

    default UUID mapUuid(String id) {
        return UUID.fromString(id);
    }

    @Named("mapGroups")
    default List<String> mapGroups(List<String> groups) {
        return groups.stream()
                .map(group -> group.replace("/", ""))
                .toList();
    }
}
