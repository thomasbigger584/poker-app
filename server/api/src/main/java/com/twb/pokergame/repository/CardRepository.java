package com.twb.pokergame.repository;

import com.twb.pokergame.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CardRepository extends JpaRepository<Card, UUID> {

    @Query("SELECT c " +
            "FROM Card c " +
            "WHERE c.round.id = :roundId " +
            "AND (c.cardType <> com.twb.pokergame.domain.enumeration.CardType.PLAYER_CARD_1 " +
            " OR c.cardType <> com.twb.pokergame.domain.enumeration.CardType.PLAYER_CARD_2)")
    List<Card> findCommunityCardsForRound(@Param("roundId") UUID roundId);

    @Query("SELECT c " +
            "FROM Card c " +
            "WHERE c.hand.id = :handId ")
    List<Card> findCardsForHand(@Param("handId") UUID handId);
}
