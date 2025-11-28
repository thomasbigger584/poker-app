package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.BettingRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BettingRoundRepository extends JpaRepository<BettingRound, UUID> {

    @Query("""
            SELECT b
            FROM BettingRound b
            WHERE b.round.pokerTable.id = :tableId
            AND b.state = com.twb.pokerapp.domain.enumeration.BettingRoundState.IN_PROGRESS
            """)
    Optional<BettingRound> findLatestInProgress(@Param("tableId") UUID tableId);
}
