package com.twb.pokergame.repository;

import com.twb.pokergame.domain.PlayerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerSessionRepository extends JpaRepository<PlayerSession, UUID> {

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "AND s.user.username = :username " +
            "AND s.connectionState = com.twb.pokergame.domain.enumeration.ConnectionState.CONNECTED")
    Optional<PlayerSession> findConnectedByTableIdAndUsername(@Param("tableId") UUID tableId, @Param("username") String username);

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "AND s.connectionState = com.twb.pokergame.domain.enumeration.ConnectionState.CONNECTED")
    List<PlayerSession> findConnectedByTableId(@Param("tableId") UUID tableId);
}
