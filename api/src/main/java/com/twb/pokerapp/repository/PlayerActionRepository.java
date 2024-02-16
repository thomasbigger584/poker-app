package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PlayerAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PlayerActionRepository extends JpaRepository<PlayerAction, UUID> {

    @Query("SELECT a " +
            "FROM PlayerAction a " +
            "WHERE a.round.id = :roundId " +
            "AND a.playerSession.id = :playerSessionId")
    Optional<PlayerAction> findByRoundAndPlayerSession(@Param("roundId") UUID roundId,
                                                       @Param("playerSessionId") UUID playerSessionId);
}
