package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.domain.enumeration.ConnectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
            AND s.funds IS NOT NULL AND s.funds > 0
            ORDER BY s.position ASC
            """)
    List<PlayerSession> findConnectedPlayersByTableId(@Param("tableId") UUID tableId);

    @Query("""
            SELECT count(s)
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            AND s.funds IS NOT NULL AND s.funds > 0
            """)
    int countConnectedPlayersByTableId(@Param("tableId") UUID tableId);

    @Query("""
            SELECT count(s)
            FROM PlayerSession s
            WHERE s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = :connectionType
            """)
    int countConnected(@Param("connectionType") ConnectionType connectionType);

    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.pokerTable.id = :tableId
            AND s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            AND s.round.id = :roundId
            AND s.active = true
            ORDER BY s.position ASC
            """)
    List<PlayerSession> findActivePlayersByTableId(@Param("tableId") UUID tableId, @Param("roundId") UUID roundId);


    @Query("""
            SELECT s
            FROM PlayerSession s
            WHERE s.sessionState = com.twb.pokerapp.domain.enumeration.SessionState.CONNECTED
            AND s.connectionType = com.twb.pokerapp.domain.enumeration.ConnectionType.PLAYER
            AND s.round.id = :roundId
            ORDER BY s.position ASC
            """)
    List<PlayerSession> findPlayersOnRound(@Param("roundId") UUID roundId);

}
