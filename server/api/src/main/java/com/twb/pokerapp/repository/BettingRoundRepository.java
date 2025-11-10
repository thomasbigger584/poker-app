package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.BettingRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BettingRoundRepository extends JpaRepository<BettingRound, UUID> {

}
