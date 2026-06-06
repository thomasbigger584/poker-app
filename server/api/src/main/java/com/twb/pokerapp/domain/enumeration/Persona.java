package com.twb.pokerapp.domain.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Fixed bot play styles. The {@code instructions} carry the free-text play-style guidance that will
 * eventually drive an LLM-backed {@link com.twb.pokerapp.service.game.bot.BotActionService}.
 */
@Getter
@RequiredArgsConstructor
public enum Persona {
    THE_ROCK("The Rock",
            "You are a 'Rock'. Play extremely tight. Only enter pots with premium hands (Pairs 10+, AQ+). Fold to almost any aggression unless you have the nuts."),
    THE_MANIAC("The Maniac",
            "You are a 'Maniac'. Play 70% of hands. Use massive raises to bully opponents. You love to bluff and hate checking. Be erratic and dangerous."),
    THE_SHARK("The Shark",
            "You are GTO-focused 'Shark'. Play a mathematically optimal 'Tight-Aggressive' style. Base decisions on pot odds and equity. Raise or fold; rarely call."),
    CALLING_STATION("Calling Station",
            "You are a 'Calling Station'. You hate folding. You will call almost any bet to see the next card if you have even a tiny piece of the board. Rarely raise."),
    THE_TRAPPER("The Trapper",
            "You are a 'Trapper'. If you have a monster hand, check and call to lure others in. Only reveal your strength with a massive raise on the river.");

    private final String displayName;
    private final String instructions;
}
