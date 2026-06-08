package com.twb.pokerapp.web.websocket.message.server.payload;

import com.twb.pokerapp.dto.bettinground.BettingRoundDTO;
import com.twb.pokerapp.dto.card.CardDTO;
import com.twb.pokerapp.dto.playersession.PlayerSessionDTO;
import com.twb.pokerapp.dto.round.RoundDTO;
import com.twb.pokerapp.dto.roundpot.RoundPotDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Snapshot of the in-progress hand, sent alongside {@link PlayerSubscribedDTO} when a client
 * (re)subscribes mid-round, so it can re-render the hand exactly where it left off (cards on the
 * table, pot, whose turn) — e.g. after an app restart / reconnect. Null on the wire when no hand
 * is currently being played.
 */
@Data
public class RoundStateDTO {
    private RoundDTO round;
    private PlayerSessionDTO dealer;
    private List<DealPlayerCardDTO> playerCards = new ArrayList<>();
    private List<CardDTO> communityCards = new ArrayList<>();
    private BettingRoundDTO bettingRound;
    private List<RoundPotDTO> roundPots = new ArrayList<>();
    /** Players who were dealt into this hand but are no longer active (folded / dropped out). */
    private List<PlayerSessionDTO> foldedPlayers = new ArrayList<>();
    private PlayerTurnDTO currentTurn;
}
