package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PlayerSession;
import jakarta.persistence.LockModeType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerSessionRepository extends JpaRepository<PlayerSession, UUID> {

    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.user.username = :username
            """)
    Optional<PlayerSession> findByTableIdAndUsername(@Param("tableId") UUID tableId,
                                                     @Param("username") String username);

    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            ORDER BY s.position ASC
            """)
    List<PlayerSession> findConnectedByTableId(@Param("tableId") UUID tableId);

    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            ORDER BY s.position ASC
            """)
    List<PlayerSession> findConnectedPlayersByTableId(@Param("tableId") UUID tableId);

    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            ORDER BY s.position ASC
            """)
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<PlayerSession> findConnectedPlayersByTableId_Lock(@Param("tableId") UUID tableId);

    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            AND s.funds > 0
            AND NOT EXISTS (
                SELECT 1
                FROM PlayerAction a
                WHERE a.playerSession = s
                AND a.bettingRound.round.id = :roundId
                AND a.actionType = com.twb.pokerapp.domain.enumeration.ActionType.FOLD
            )
            ORDER BY s.position ASC
            """)
    List<PlayerSession> findActivePlayersByTableId(@Param("tableId") UUID tableId, @Param("roundId") UUID roundId);

    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            AND s.funds > 0
            AND NOT EXISTS (
                SELECT 1
                FROM PlayerAction a
                WHERE a.playerSession = s
                AND a.bettingRound.round.id = :roundId
                AND a.actionType = com.twb.pokerapp.domain.enumeration.ActionType.FOLD
            )
            ORDER BY s.position ASC
            """)
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<PlayerSession> findActivePlayersByTableId_Lock(@Param("tableId") UUID tableId, @Param("roundId") UUID roundId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE PlayerSession s
            SET s.dealer = false
            WHERE s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            AND s.pokerTable.id = :tableId
            """)
    void resetDealerForTableId(@Param("tableId") UUID tableId);

    @Modifying(flushAutomatically = true)
    @Query("""
            UPDATE PlayerSession s
            SET s.dealer = :dealer
            WHERE s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            AND s.id = :id
            """)
    void setDealer(@Param("id") UUID id, @Param("dealer") boolean dealer);

    @Query("""
            SELECT count(s)
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            """)
    int countConnectedPlayersByTableId(@Param("tableId") UUID tableId);
}
