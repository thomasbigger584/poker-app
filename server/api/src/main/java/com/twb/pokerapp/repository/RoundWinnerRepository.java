package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.RoundWinner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoundWinnerRepository extends JpaRepository<RoundWinner, UUID> {

    @Query("""
            SELECT w
            FROM RoundWinner w
            WHERE w.round.id = :roundId
            """)
    List<RoundWinner> findByRound(@Param("roundId") UUID roundId);

    @Query("""
            SELECT w
            FROM RoundWinner w
            WHERE w.round.id = :roundId
            AND w.playerSession.id = :playerSessionId
            """)
    Optional<RoundWinner> findByRoundAndPlayerSession(@Param("roundId") UUID roundId, @Param("playerSessionId") UUID playerSessionId);
}
