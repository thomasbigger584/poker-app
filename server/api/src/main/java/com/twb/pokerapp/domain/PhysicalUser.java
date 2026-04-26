package com.twb.pokerapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "physical_user")
public class PhysicalUser extends AppUser {

    @Column(name = "email")
    private String email;

    @Column(name = "email_verified")
    private boolean emailVerified;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "groups", columnDefinition = "jsonb")
    private List<String> groups = new ArrayList<>();

    @Override
    public String toString() {
        return "PhysicalUser{" +
                "email='" + email + '\'' +
                ", emailVerified=" + emailVerified +
                ", groups=" + groups +
                "} " + super.toString();
    }
}
