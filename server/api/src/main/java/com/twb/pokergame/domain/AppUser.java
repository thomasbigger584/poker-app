package com.twb.pokergame.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "app_user")
public class AppUser {

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
    private List<String> groups = new ArrayList<>();

    // -----------------------------------------------------------------

    @Column(name = "total_funds")
    private double totalFunds = 0d;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PlayerSession playerSession;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppUser appUser = (AppUser) o;
        return new EqualsBuilder().append(id, appUser.id).append(username, appUser.username).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id).append(username).toHashCode();
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", enabled=" + enabled +
                ", groups=" + groups +
                ", totalFunds=" + totalFunds +
                '}';
    }
}
