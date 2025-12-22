package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PlayerAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerActionRepository extends JpaRepository<PlayerAction, UUID> {

    @Query("""
            SELECT a
            FROM PlayerAction a
            WHERE a.bettingRound.id = :bettingRoundId
            AND a.actionType <> com.twb.pokerapp.domain.enumeration.ActionType.FOLD
            ORDER BY a.createdDateTime DESC
            """)
    List<PlayerAction> findPlayerActionsNotFolded(@Param("bettingRoundId") UUID bettingRoundId);

    @Query("""
            SELECT a
            FROM PlayerAction a
            WHERE a.bettingRound.id = :bettingRoundId
            AND a.playerSession.id = :playerSessionId
            AND a.createdDateTime = (
                SELECT MAX(a2.createdDateTime)
                FROM PlayerAction a2
                WHERE a2.bettingRound.id = :bettingRoundId
                AND a2.playerSession.id = :playerSessionId
            )
            """)
    Optional<PlayerAction> findByBettingRoundAndPlayer(@Param("bettingRoundId") UUID bettingRoundId, @Param("playerSessionId") UUID playerSessionId);
}
