package com.twb.pokerapp.domain;

import com.twb.pokerapp.domain.enumeration.Persona;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bot_user")
public class BotUser extends AppUser {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "persona")
    private Persona persona;

    @Override
    public String toString() {
        return "BotUser{" +
                "persona=" + persona +
                "} " + super.toString();
    }
}
