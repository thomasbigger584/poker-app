package com.twb.pokerapp.service.user;

import com.twb.pokerapp.domain.BotUser;
import com.twb.pokerapp.domain.enumeration.Persona;
import com.twb.pokerapp.repository.BotUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BotUserService {
    // Bots have no funds of their own (totalFunds lives on PhysicalUser); their ability to join a
    // table is not gated on funds (see TableGameService#onBotConnected).

    private final BotUserRepository botUserRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void init() {
        if (botUserRepository.count() > 0) return;

        create("stone_cold", "Steve", "Stone", Persona.THE_ROCK);
        create("wild_bill", "Bill", "Wild", Persona.THE_MANIAC);
        create("algo_al", "Al", "Gorithm", Persona.THE_SHARK);
        create("sticky_stan", "Stan", "Sticky", Persona.CALLING_STATION);
        create("silent_sarah", "Sarah", "Silent", Persona.THE_TRAPPER);
    }

    private void create(String username, String firstName, String lastName, Persona persona) {
        var bot = new BotUser();
        bot.setId(UUID.randomUUID());
        bot.setUsername(username);
        bot.setFirstName(firstName);
        bot.setLastName(lastName);
        bot.setEnabled(true);
        bot.setPersona(persona);
        botUserRepository.save(bot);
    }
}
