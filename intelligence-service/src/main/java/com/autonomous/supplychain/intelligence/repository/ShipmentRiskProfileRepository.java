package com.autonomous.supplychain.intelligence.repository;

import com.autonomous.supplychain.intelligence.domain.ShipmentRiskProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentRiskProfileRepository extends JpaRepository<ShipmentRiskProfile, String> {
    List<ShipmentRiskProfile> findTop20ByOrderByRiskScoreDesc();

    List<ShipmentRiskProfile> findByRiskScoreGreaterThanEqualOrderByRiskScoreDesc(double threshold);

    List<ShipmentRiskProfile> findTop10ByOrderByUpdatedAtDesc();
}
