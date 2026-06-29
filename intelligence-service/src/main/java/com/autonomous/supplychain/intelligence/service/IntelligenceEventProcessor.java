package com.autonomous.supplychain.intelligence.service;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.intelligence.domain.BottleneckObservation;
import com.autonomous.supplychain.intelligence.domain.ShipmentRiskProfile;
import com.autonomous.supplychain.intelligence.repository.BottleneckObservationRepository;
import com.autonomous.supplychain.intelligence.repository.ShipmentRiskProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IntelligenceEventProcessor {
    private final DelayRiskScorer delayRiskScorer;
    private final ShipmentRiskProfileRepository riskProfileRepository;
    private final BottleneckObservationRepository bottleneckRepository;

    public IntelligenceEventProcessor(
            DelayRiskScorer delayRiskScorer,
            ShipmentRiskProfileRepository riskProfileRepository,
            BottleneckObservationRepository bottleneckRepository
    ) {
        this.delayRiskScorer = delayRiskScorer;
        this.riskProfileRepository = riskProfileRepository;
        this.bottleneckRepository = bottleneckRepository;
    }

    @Transactional
    public ShipmentRiskProfile process(ShipmentEvent event) {
        RiskAssessment assessment = delayRiskScorer.assess(event);
        ShipmentRiskProfile profile = riskProfileRepository.findById(event.shipmentId())
                .orElseGet(() -> new ShipmentRiskProfile(event.shipmentId()));
        profile.apply(event, assessment);
        ShipmentRiskProfile saved = riskProfileRepository.save(profile);
        updateBottleneck(event);
        return saved;
    }

    private void updateBottleneck(ShipmentEvent event) {
        if (event.eventType() != EventType.EXCEPTION_RAISED || event.currentLocation() == null) {
            return;
        }
        String locationName = event.currentLocation().name();
        if (locationName == null || locationName.isBlank()) {
            return;
        }
        ExceptionCode exceptionCode = event.exceptionCode() == null ? ExceptionCode.UNKNOWN : event.exceptionCode();
        BottleneckObservation observation = bottleneckRepository.findByLocationNameAndExceptionCode(locationName, exceptionCode)
                .orElseGet(() -> new BottleneckObservation(locationName, exceptionCode));
        observation.recordImpact(event.dwellMinutes(), event.severity());
        bottleneckRepository.save(observation);
    }
}
