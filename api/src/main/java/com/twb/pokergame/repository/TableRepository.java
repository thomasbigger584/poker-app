package com.twb.pokergame.repository;

import com.twb.pokergame.domain.PokerTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TableRepository extends JpaRepository<PokerTable, UUID> {
}
