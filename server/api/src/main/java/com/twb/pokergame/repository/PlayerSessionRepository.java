package com.twb.pokergame.repository;

import com.twb.pokergame.domain.PlayerSession;
import jakarta.persistence.LockModeType;
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

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "AND s.user.username = :username ")
    Optional<PlayerSession> findByTableIdAndUsername(@Param("tableId") UUID tableId,
                                                     @Param("username") String username);

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.id = :id " +
            "AND s.sessionState = com.twb.pokergame.domain.enumeration.SessionState.CONNECTED ")
    Optional<PlayerSession> findConnectedById(@Param("id") UUID id);

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "ORDER BY s.position ASC ")
    List<PlayerSession> findByTableId(@Param("tableId") UUID tableId);

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "AND s.sessionState = com.twb.pokergame.domain.enumeration.SessionState.CONNECTED " +
            "ORDER BY s.position ASC ")
    List<PlayerSession> findConnectedByTableId(@Param("tableId") UUID tableId);

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "AND s.sessionState = com.twb.pokergame.domain.enumeration.SessionState.CONNECTED " +
            "AND s.connectionType = com.twb.pokergame.domain.enumeration.ConnectionType.PLAYER " +
            "ORDER BY s.position ASC ")
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<PlayerSession> findConnectedPlayersByTableId(@Param("tableId") UUID tableId);

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "AND s.sessionState = com.twb.pokergame.domain.enumeration.SessionState.CONNECTED " +
            "AND s.connectionType = com.twb.pokergame.domain.enumeration.ConnectionType.PLAYER " +
            "AND s.user.username != :username " +
            "ORDER BY s.position ASC ")
    List<PlayerSession> findOtherConnectedPlayersByTableId(@Param("tableId") UUID tableId, @Param("username") String username);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE PlayerSession s " +
            "SET s.dealer = false " +
            "WHERE s.sessionState = com.twb.pokergame.domain.enumeration.SessionState.CONNECTED " +
            "AND s.connectionType = com.twb.pokergame.domain.enumeration.ConnectionType.PLAYER " +
            "AND s.id IN :notDealerIdList " )
    void updateNotDealer(@Param("notDealerIdList") List<UUID> notDealerIdList);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE PlayerSession s " +
            "SET s.dealer = true " +
            "WHERE s.sessionState = com.twb.pokergame.domain.enumeration.SessionState.CONNECTED " +
            "AND s.connectionType = com.twb.pokergame.domain.enumeration.ConnectionType.PLAYER " +
            "AND s.id = :id " )
    void updateDealer(@Param("id") UUID id);
}
