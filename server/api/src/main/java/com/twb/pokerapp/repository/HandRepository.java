package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.Hand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
    Optional<Hand> findHandForRound(@Param("playerSessionId") UUID playerSessionId, @Param("roundId") UUID roundId);
}
