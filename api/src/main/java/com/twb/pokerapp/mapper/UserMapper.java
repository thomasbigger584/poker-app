package com.twb.pokerapp.mapper;

import com.twb.pokerapp.configuration.Constants;
import com.twb.pokerapp.domain.AppUser;
import org.keycloak.representations.idm.UserRepresentation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "groups", target = "groups", qualifiedByName = "mapGroups")
    AppUser representationToModel(UserRepresentation representation);

    default UUID mapUuid(String id) {
        return UUID.fromString(id);
    }

    @Named("mapGroups")
    default List<String> mapGroups(List<String> groups) {
        if (groups == null || groups.isEmpty()) {
            return Collections.singletonList(Constants.USER);
        }
        return groups.stream()
                .map(group -> group.replace("/", ""))
                .toList();
    }
}
