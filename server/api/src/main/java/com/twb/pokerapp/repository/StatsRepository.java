package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PlayerSession;
import com.twb.pokerapp.proto.HandType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Read-only aggregate queries that back the player statistics endpoint. Each
 * method rolls a single metric up across the gameplay tables for one user,
 * resolved by username (the JWT subject). SUM/MAX return {@code null} when the
 * player has no matching rows — the service coalesces to zero.
 */
@Repository
public interface StatsRepository extends JpaRepository<PlayerSession, UUID> {

    // --- Volume / performance -------------------------------------------------

    @Query("SELECT COUNT(h) FROM Hand h WHERE h.playerSession.user.username = :username")
    long countHandsPlayed(@Param("username") String username);

    @Query("SELECT COUNT(DISTINCT w.round.id) FROM RoundWinner w WHERE w.playerSession.user.username = :username")
    long countRoundsWon(@Param("username") String username);

    @Query("SELECT COUNT(DISTINCT s.pokerTable.id) FROM PlayerSession s WHERE s.user.username = :username")
    long countTablesJoined(@Param("username") String username);

    // --- Bankroll -------------------------------------------------------------

    @Query("SELECT SUM(w.amount) FROM RoundWinner w WHERE w.playerSession.user.username = :username")
    BigDecimal sumWinnings(@Param("username") String username);

    @Query("SELECT MAX(w.amount) FROM RoundWinner w WHERE w.playerSession.user.username = :username")
    BigDecimal maxWinning(@Param("username") String username);

    @Query("SELECT SUM(a.amount) FROM PlayerAction a WHERE a.playerSession.user.username = :username")
    BigDecimal sumWagered(@Param("username") String username);

    @Query("""
            SELECT SUM(t.amount)
            FROM TransactionHistory t
            WHERE t.user.username = :username
            AND t.type = com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_BUYIN
            """)
    BigDecimal sumBuyIns(@Param("username") String username);

    @Query("""
            SELECT SUM(t.amount)
            FROM TransactionHistory t
            WHERE t.user.username = :username
            AND t.type = com.twb.pokerapp.proto.TransactionHistoryType.TRANSACTION_HISTORY_TYPE_CASHOUT
            """)
    BigDecimal sumCashOuts(@Param("username") String username);

    // --- Action breakdown / highlights ---------------------------------------

    /** Rows of {@code [ActionType, Long count]} for the player's actions. */
    @Query("""
            SELECT a.actionType, COUNT(a)
            FROM PlayerAction a
            WHERE a.playerSession.user.username = :username
            GROUP BY a.actionType
            """)
    List<Object[]> actionBreakdown(@Param("username") String username);

    /** Distinct hand types the player has made (best is chosen in the service). */
    @Query("""
            SELECT DISTINCT h.handType
            FROM Hand h
            WHERE h.playerSession.user.username = :username
            AND h.handType IS NOT NULL
            """)
    List<HandType> distinctHandTypes(@Param("username") String username);
}
