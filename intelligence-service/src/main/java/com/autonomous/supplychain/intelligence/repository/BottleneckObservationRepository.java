package com.autonomous.supplychain.intelligence.repository;

import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.intelligence.domain.BottleneckObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BottleneckObservationRepository extends JpaRepository<BottleneckObservation, UUID> {
    Optional<BottleneckObservation> findByLocationNameAndExceptionCode(String locationName, ExceptionCode exceptionCode);

    List<BottleneckObservation> findTop20ByOrderByShipmentsImpactedDescUpdatedAtDesc();
}
