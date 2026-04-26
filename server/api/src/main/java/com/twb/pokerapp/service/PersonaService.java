package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.BotUser;
import com.twb.pokerapp.domain.Persona;
import com.twb.pokerapp.repository.PersonaRepository;
import com.twb.pokerapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonaService {
    private final PersonaRepository personaRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void init() {
        if (personaRepository.count() > 0) return;

        create("stone_cold_steve", "Steve", "Stone", "The Rock", "You are a 'Rock'. Play extremely tight. Only enter pots with premium hands (Pairs 10+, AQ+). Fold to almost any aggression unless you have the nuts.");
        create("wild_bill_poker", "Bill", "Wild", "The Maniac", "You are a 'Maniac'. Play 70% of hands. Use massive raises to bully opponents. You love to bluff and hate checking. Be erratic and dangerous.");
        create("algorithm_al", "Al", "Gorithm", "The Shark", "You are GTO-focused 'Shark'. Play a mathematically optimal 'Tight-Aggressive' style. Base decisions on pot odds and equity. Raise or fold; rarely call.");
        create("sticky_stan", "Stan", "Sticky", "Calling Station", "You are a 'Calling Station'. You hate folding. You will call almost any bet to see the next card if you have even a tiny piece of the board. Rarely raise.");
        create("silent_sarah", "Sarah", "Silent", "The Trapper", "You are a 'Trapper'. If you have a monster hand, check and call to lure others in. Only reveal your strength with a massive raise on the river.");
    }

    private void create(String username, String firstName, String lastName, String personaName, String instructions) {
        var persona = new Persona();
        persona.setName(personaName);
        persona.setInstructions(instructions);
        persona = personaRepository.save(persona);

        var bot = new BotUser();
        bot.setId(UUID.randomUUID());
        bot.setUsername(username);
        bot.setFirstName(firstName);
        bot.setLastName(lastName);
        bot.setEnabled(true);
        bot.setPersona(persona);
        userRepository.save(bot);
    }
}
