package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.RoundPot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoundPotRepository extends JpaRepository<RoundPot, UUID> {

}
