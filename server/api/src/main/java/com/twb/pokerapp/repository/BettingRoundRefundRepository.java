package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.BettingRoundRefund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BettingRoundRefundRepository extends JpaRepository<BettingRoundRefund, UUID> {

    @Query("""
             SELECT b
             FROM BettingRoundRefund b
             WHERE b.bettingRound.round.id = :roundId
            """)
    List<BettingRoundRefund> findByRoundId(@Param("roundId") UUID roundId);
}
