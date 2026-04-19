package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.RoundPot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoundPotRepository extends JpaRepository<RoundPot, UUID> {

    @Query("""
            SELECT r
            FROM RoundPot r
            WHERE r.round.id = :roundId
            ORDER BY r.potIndex ASC
            """)
    List<RoundPot> findByRound(@Param("roundId") UUID roundId);
}
