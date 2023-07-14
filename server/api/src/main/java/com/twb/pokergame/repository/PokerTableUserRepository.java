package com.twb.pokergame.repository;

import com.twb.pokergame.domain.PokerTableUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PokerTableUserRepository extends JpaRepository<PokerTableUser, UUID> {
    List<PokerTableUser> findByPokerTableId(UUID pokerTableId);
}
