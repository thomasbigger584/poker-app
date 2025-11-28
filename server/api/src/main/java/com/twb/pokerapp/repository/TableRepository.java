package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.PokerTable;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableRepository extends JpaRepository<PokerTable, UUID> {

    @Query("""
            SELECT t
            FROM PokerTable t
            WHERE t.id = :tableId
            """)
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<PokerTable> findById_Lock(@Param("tableId") UUID tableId);
}
