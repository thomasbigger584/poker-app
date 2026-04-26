package com.twb.pokerapp.repository;

import com.twb.pokerapp.domain.Temperament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TemperamentRepository extends JpaRepository<Temperament, UUID> {
}
