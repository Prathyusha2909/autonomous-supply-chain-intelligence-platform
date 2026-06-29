package com.autonomous.supplychain.intelligence.service;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DelayRiskScorerTest {
    private final DelayRiskScorer scorer = new DelayRiskScorer();

    @Test
    void raisesRiskForCriticalExceptionAndEtaSlip() {
        Instant planned = Instant.parse("2026-07-01T10:00:00Z");
        ShipmentEvent event = ShipmentEvent.builder("SHP-100", EventType.EXCEPTION_RAISED)
                .plannedEta(planned)
                .predictedEta(planned.plusSeconds(10 * 60 * 60))
                .dwellMinutes(600)
                .severity(Severity.CRITICAL)
                .exceptionCode(ExceptionCode.CUSTOMS_HOLD)
                .build();

        RiskAssessment assessment = scorer.assess(event);

        assertThat(assessment.riskScore()).isGreaterThan(0.75);
        assertThat(assessment.delayMinutes()).isEqualTo(600);
        assertThat(assessment.rootCause()).contains("Customs");
        assertThat(assessment.recommendation()).contains("Escalate");
    }

    @Test
    void keepsLowRiskForNormalLocationUpdate() {
        ShipmentEvent event = ShipmentEvent.builder("SHP-200", EventType.LOCATION_UPDATED)
                .severity(Severity.LOW)
                .build();

        RiskAssessment assessment = scorer.assess(event);

        assertThat(assessment.riskScore()).isLessThan(0.2);
        assertThat(assessment.delayMinutes()).isZero();
    }
}
