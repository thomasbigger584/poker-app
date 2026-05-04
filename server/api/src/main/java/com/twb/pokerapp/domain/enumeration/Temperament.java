package com.twb.pokerapp.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Temperament {
    NEUTRAL(0.00f, 0.70f, null),
    ON_TILT(0.70f, 0.85f, "MODIFIER: You are currently on tilt. You are impatient, frustrated, and much more likely to make a reckless, high-risk bluff."),
    PARANOID(0.85f, 0.93f, "MODIFIER: You are feeling paranoid. You suspect your opponents are slow-playing a monster hand. Play more cautiously than usual."),
    BORED(0.93f, 0.98f, "MODIFIER: You are feeling bored. You are willing to play a trash hand just to see a flop and get some action started."),
    INVINCIBLE(0.98f, 1.00f, "MODIFIER: You feel invincible. You believe you can read everyone's soul. Play with extreme confidence and execute a high-IQ deceptive play.");

    private final float fromRoll;
    private final float toRoll;
    private final String modifier;

    public static Temperament fromRoll(float roll) {
        for (Temperament temperament : values()) {
            if (roll >= temperament.fromRoll && roll < temperament.toRoll) {
                return temperament;
            }
        }
        return NEUTRAL;
    }
}
