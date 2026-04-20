package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.TransactionHistory;
import com.twb.pokerapp.domain.enumeration.TransactionHistoryType;
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
            AND (:type IS NULL OR t.type = :type)
            ORDER BY t.createdDateTime DESC
            """)
    Page<TransactionHistory> findByUsernameAndType(@Param("username") String username,
                                                   @Param("type") TransactionHistoryType type,
                                                   Pageable pageable);
}
