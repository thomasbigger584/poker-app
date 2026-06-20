package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.Hand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HandRepository extends JpaRepository<Hand, UUID> {

    @Query("""
            SELECT h
            FROM Hand h
            WHERE h.playerSession.id = :playerSessionId
            AND h.round.id = :roundId
            """)
    Optional<Hand> findForPlayerAndRound(@Param("playerSessionId") UUID playerSessionId, @Param("roundId") UUID roundId);

    /**
     * Seat positions that were dealt into the table's current (still in-progress) hand — including a
     * player who has explicitly left but whose hand has not yet finished. Their seat stays reserved
     * (not handed to a new player) until the hand completes, so a freed seat can't be re-occupied
     * mid-hand. The {@code Hand} rows retain the round link even after the session is detached.
     */
    @Query("""
            SELECT DISTINCT h.playerSession.position
            FROM Hand h
            WHERE h.round.pokerTable.id = :tableId
            AND h.round.roundState <> com.twb.pokerapp.proto.RoundState.ROUND_STATE_FINISHED
            AND h.round.roundState <> com.twb.pokerapp.proto.RoundState.ROUND_STATE_FAILED
            AND h.playerSession.position IS NOT NULL
            """)
    List<Integer> findOccupiedPositionsInCurrentRound(@Param("tableId") UUID tableId);
}
