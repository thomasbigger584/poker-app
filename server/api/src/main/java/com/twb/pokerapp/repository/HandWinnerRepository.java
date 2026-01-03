package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.HandWinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HandWinnerRepository extends JpaRepository<HandWinner, UUID> {

    @Query("""
            SELECT h
            FROM HandWinner h
            WHERE h.round.id = :roundId
            """)
    List<HandWinner> findByRound(@Param("roundId") UUID roundId);

    @Query("""
            SELECT h
            FROM HandWinner h
            WHERE h.round.id = :roundId
            AND h.playerSession.id = :playerSessionId
            """)
    Optional<HandWinner> findByRoundAndPlayerSession(@Param("roundId") UUID roundId, @Param("playerSessionId") UUID playerSessionId);
}
