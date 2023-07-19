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
            "AND s.user.username = :username ")
    Optional<PlayerSession> findByTableIdAndUsername(@Param("tableId") UUID tableId,
                                                     @Param("username") String username);

    @Query("SELECT s " +
            "FROM PlayerSession s " +
            "WHERE s.pokerTable.id = :tableId " +
            "ORDER BY s.position ASC ")
    List<PlayerSession> findByTableId(@Param("tableId") UUID tableId);
}
