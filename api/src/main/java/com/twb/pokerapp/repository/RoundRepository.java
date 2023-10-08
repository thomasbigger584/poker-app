package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoundRepository extends JpaRepository<Round, UUID> {

    @Query("SELECT r " +
            "FROM Round r " +
            "WHERE r.pokerTable.id = :tableId")
    List<Round> findByTableId(UUID tableId);

    @Query("SELECT r " +
            "FROM Round r " +
            "WHERE r.roundState <> com.twb.pokerapp.domain.enumeration.RoundState.FINISH")
    List<Round> findAllNotFinished();

    @Query("SELECT r " +
            "FROM Round r " +
            "WHERE r.pokerTable.id = :tableId " +
            "AND r.roundState <> com.twb.pokerapp.domain.enumeration.RoundState.FINISH")
    Optional<Round> findCurrentByTableId(@Param("tableId") UUID tableId);
}
