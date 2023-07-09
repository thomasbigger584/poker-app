package com.twb.pokergame.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "app_user")
public class User {

    @Id
    @NotNull
    @Column(name = "id")
    private UUID id;

    @NotNull
    @Column(name = "username")
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @Column(name = "enabled")
    private boolean enabled;

    @NotNull
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "groups", columnDefinition = "jsonb")
    private List<String> groups;
}
