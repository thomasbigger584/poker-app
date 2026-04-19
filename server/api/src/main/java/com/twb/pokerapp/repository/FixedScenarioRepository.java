package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.FixedScenario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FixedScenarioRepository extends JpaRepository<FixedScenario, UUID> {
    Optional<FixedScenario> findTopBy();
}
