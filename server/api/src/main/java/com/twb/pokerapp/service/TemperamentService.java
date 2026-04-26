package com.twb.pokerapp.service;

import com.twb.pokerapp.domain.Temperament;
import com.twb.pokerapp.repository.TemperamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TemperamentService {
    private final TemperamentRepository temperamentRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public void init() {
        if (temperamentRepository.count() > 0) return;

        create(0.00f, 0.70f, null);
        create(0.70f, 0.85f, "MODIFIER: You are currently on tilt. You are impatient, frustrated, and much more likely to make a reckless, high-risk bluff.");
        create(0.85f, 0.93f, "MODIFIER: You are feeling paranoid. You suspect your opponents are slow-playing a monster hand. Play more cautiously than usual.");
        create(0.93f, 0.98f, "MODIFIER: You are feeling bored. You are willing to play a trash hand just to see a flop and get some action started.");
        create(0.98f, 1.00f, "MODIFIER: You feel invincible. You believe you can read everyone's soul. Play with extreme confidence and execute a high-IQ deceptive play.");
    }

    private void create(float from, float to, String modifier) {
        var temperament = new Temperament();
        temperament.setId(UUID.randomUUID());
        temperament.setFromRoll(from);
        temperament.setToRoll(to);
        temperament.setModifier(modifier);
        temperamentRepository.save(temperament);
    }
}
