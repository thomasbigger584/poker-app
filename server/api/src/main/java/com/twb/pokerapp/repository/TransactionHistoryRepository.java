package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.TransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, UUID> {

    @Query("""
            SELECT t
            FROM TransactionHistory t
            JOIN t.user u
            WHERE u.username = :username
            ORDER BY t.createdDateTime DESC
            """)
    Page<TransactionHistory> findByUsername(@Param("username") String username, Pageable pageable);

    @Query(value = """
            WITH RankedTransactions AS (
                SELECT t.*,
                       LAG(t.amount) OVER (PARTITION BY t.user_id ORDER BY t.id) as prev_amt,
                       LAG(t.type) OVER (PARTITION BY t.user_id ORDER BY t.id) as prev_type,
                       LEAD(t.amount) OVER (PARTITION BY t.user_id ORDER BY t.id) as next_amt,
                       LEAD(t.type) OVER (PARTITION BY t.user_id ORDER BY t.id) as next_type
                FROM transaction_history t
                JOIN app_user u ON t.user_id = u.id
                WHERE u.username = :username
            )
            SELECT id, user_id, amount, type, created_date_time, modified_date_time
            FROM RankedTransactions
            WHERE NOT (amount = next_amt AND (
                (type = 'BUYIN' AND next_type = 'CASHOUT') OR (type = 'CASHOUT' AND next_type = 'BUYIN') OR
                (type = 'DEPOSIT' AND next_type = 'WITHDRAW') OR (type = 'WITHDRAW' AND next_type = 'DEPOSIT')
            ))
            AND NOT (amount = prev_amt AND (
                (type = 'BUYIN' AND prev_type = 'CASHOUT') OR (type = 'CASHOUT' AND prev_type = 'BUYIN') OR
                (type = 'DEPOSIT' AND prev_type = 'WITHDRAW') OR (type = 'WITHDRAW' AND prev_type = 'DEPOSIT')
            ))
            ORDER BY id DESC
            """,
            countQuery = """
            SELECT count(*) FROM (
                WITH RankedTransactions AS (
                    SELECT t.amount, t.type, t.user_id, t.id,
                           LAG(t.amount) OVER (PARTITION BY t.user_id ORDER BY t.id) as prev_amt,
                           LAG(t.type) OVER (PARTITION BY t.user_id ORDER BY t.id) as prev_type,
                           LEAD(t.amount) OVER (PARTITION BY t.user_id ORDER BY t.id) as next_amt,
                           LEAD(t.type) OVER (PARTITION BY t.user_id ORDER BY t.id) as next_type
                    FROM transaction_history t
                    JOIN app_user u ON t.user_id = u.id
                    WHERE u.username = :username
                )
                SELECT id FROM RankedTransactions
                WHERE NOT (amount = next_amt AND (
                    (type = 'BUYIN' AND next_type = 'CASHOUT') OR (type = 'CASHOUT' AND next_type = 'BUYIN') OR
                    (type = 'DEPOSIT' AND next_type = 'WITHDRAW') OR (type = 'WITHDRAW' AND next_type = 'DEPOSIT')
                ))
                AND NOT (amount = prev_amt AND (
                    (type = 'BUYIN' AND prev_type = 'CASHOUT') OR (type = 'CASHOUT' AND prev_type = 'BUYIN') OR
                    (type = 'DEPOSIT' AND prev_type = 'WITHDRAW') OR (type = 'WITHDRAW' AND prev_type = 'DEPOSIT')
                ))
            ) AS filtered_transactions
            """,
            nativeQuery = true)
    Page<TransactionHistory> findSimplifiedByUsername(@Param("username") String username, Pageable pageable);
}
