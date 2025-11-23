package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PlayerAction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerActionRepository extends JpaRepository<PlayerAction, UUID> {

    @Query("""
            SELECT SUM(a.amount)
            FROM PlayerAction a
            WHERE a.bettingRound.id = :bettingRoundId
            """)
    double sumAmounts(@Param("bettingRoundId") UUID bettingRoundId);

    @Query("""
            SELECT a
            FROM PlayerAction a
            WHERE a.bettingRound.id = :bettingRoundId
            AND a.actionType <> com.twb.pokerapp.domain.enumeration.ActionType.FOLD
            ORDER BY a.id DESC
            """)
    List<PlayerAction> findPlayerActionsNotFolded(@Param("bettingRoundId") UUID bettingRoundId);
}
