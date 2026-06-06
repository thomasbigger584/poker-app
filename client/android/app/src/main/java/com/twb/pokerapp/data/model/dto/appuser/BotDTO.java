package com.twb.pokerapp.data.model.dto.appuser;

import androidx.annotation.NonNull;

import java.util.UUID;

/**
 * Mirror of the server-side {@code com.twb.pokerapp.dto.appuser.BotDTO}. Represents a selectable
 * fixed bot player that can be connected to a table via the {@code sendBotConnected} action.
 */
public class BotDTO {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String personaName;
    private String personaInstructions;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public String getPersonaInstructions() {
        return personaInstructions;
    }

    public void setPersonaInstructions(String personaInstructions) {
        this.personaInstructions = personaInstructions;
    }

    @NonNull
    @Override
    public String toString() {
        return "BotDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", personaName='" + personaName + '\'' +
                '}';
    }
}
