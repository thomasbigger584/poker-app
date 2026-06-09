package com.twb.pokerapp.mapper;

import com.twb.pokerapp.configuration.Constants;
import com.twb.pokerapp.domain.AppUser;
import com.twb.pokerapp.domain.BotUser;
import com.twb.pokerapp.domain.PhysicalUser;
import com.twb.pokerapp.proto.AppUserDTO;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
public class UserMapper {

    public PhysicalUser representationToModel(UserRepresentation representation) {
        if (representation == null) {
            return null;
        }
        var user = new PhysicalUser();
        user.setId(mapUuid(representation.getId()));
        user.setUsername(representation.getUsername());
        user.setFirstName(representation.getFirstName());
        user.setLastName(representation.getLastName());
        user.setEnabled(Boolean.TRUE.equals(representation.isEnabled()));
        user.setEmail(representation.getEmail());
        user.setEmailVerified(Boolean.TRUE.equals(representation.isEmailVerified()));
        user.setGroups(mapGroups(representation.getGroups()));
        return user;
    }

    public AppUserDTO modelToDto(AppUser appUser) {
        if (appUser == null) {
            return null;
        }
        var builder = AppUserDTO.newBuilder()
                .setId(ProtoConvert.uuidStr(appUser.getId()))
                .setUsername(ProtoConvert.text(appUser.getUsername()))
                .setFirstName(ProtoConvert.text(appUser.getFirstName()))
                .setLastName(ProtoConvert.text(appUser.getLastName()))
                .setEnabled(appUser.isEnabled());
        if (appUser instanceof PhysicalUser physical) {
            builder.setEmail(ProtoConvert.text(physical.getEmail()))
                    .setEmailVerified(physical.isEmailVerified())
                    .setTotalFunds(ProtoConvert.money(physical.getTotalFunds()));
        } else if (appUser instanceof BotUser bot && bot.getPersona() != null) {
            builder.setPersona(bot.getPersona().getDisplayName());
        }
        return builder.build();
    }

    public UUID mapUuid(String id) {
        return UUID.fromString(id);
    }

    public List<String> mapGroups(List<String> groups) {
        if (groups == null || groups.isEmpty()) {
            return Collections.singletonList(Constants.USER);
        }
        return groups.stream()
                .map(group -> group.replace("/", ""))
                .toList();
    }
}
