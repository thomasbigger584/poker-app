package com.twb.pokerapp.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "bot_user")
public class BotUser extends AppUser {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id")
    private Persona persona;

    @Override
    public String toString() {
        return "BotUser{" +
                "persona=" + (persona != null ? persona.getName() : "null") +
                "} " + super.toString();
    }
}
