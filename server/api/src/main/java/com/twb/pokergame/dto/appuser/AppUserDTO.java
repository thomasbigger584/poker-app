package com.twb.pokergame.dto.appuser;

import lombok.Data;

import java.util.UUID;


@Data
public class AppUserDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private boolean emailVerified;
    private boolean enabled;
}
