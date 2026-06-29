package com.autonomous.supplychain.intelligence.service;

import com.autonomous.supplychain.common.events.EventType;
import com.autonomous.supplychain.common.events.ShipmentEvent;
import com.autonomous.supplychain.common.model.ExceptionCode;
import com.autonomous.supplychain.common.model.Severity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class DelayRiskScorer {
    public RiskAssessment assess(ShipmentEvent event) {
        long etaDelay = etaDelayMinutes(event.plannedEta(), event.predictedEta());
        long dwellDelay = event.dwellMinutes() == null ? 0 : Math.max(0, event.dwellMinutes() - 120);
        double score = clamp(severityWeight(event.severity()) + exceptionWeight(event.exceptionCode()) + etaWeight(etaDelay) + dwellWeight(dwellDelay));

        long projectedDelay = Math.max(etaDelay, dwellDelay / 2);
        String rootCause = rootCause(event);
        String recommendation = recommendation(event, score, projectedDelay);
        return new RiskAssessment(score, projectedDelay, rootCause, recommendation);
    }

    private long etaDelayMinutes(Instant plannedEta, Instant predictedEta) {
        if (plannedEta == null || predictedEta == null || !predictedEta.isAfter(plannedEta)) {
            return 0;
        }
        return Duration.between(plannedEta, predictedEta).toMinutes();
    }

    private double severityWeight(Severity severity) {
        return switch (severity == null ? Severity.LOW : severity) {
            case LOW -> 0.08;
            case MEDIUM -> 0.25;
            case HIGH -> 0.5;
            case CRITICAL -> 0.72;
        };
    }

    private double exceptionWeight(ExceptionCode exceptionCode) {
        if (exceptionCode == null) {
            return 0.0;
        }
        return switch (exceptionCode) {
            case PORT_CONGESTION, CUSTOMS_HOLD -> 0.18;
            case WEATHER_RISK, ROUTE_DEVIATION -> 0.14;
            case TEMPERATURE_EXCURSION, MISSED_CONNECTION -> 0.22;
            case CAPACITY_SHORTAGE -> 0.16;
            case UNKNOWN -> 0.08;
        };
    }

    private double etaWeight(long etaDelayMinutes) {
        return Math.min(0.35, etaDelayMinutes / 1440.0);
    }

    private double dwellWeight(long dwellDelayMinutes) {
        return Math.min(0.2, dwellDelayMinutes / 1440.0);
    }

    private double clamp(double value) {
        return Math.max(0.0, Math.min(0.99, value));
    }

    private String rootCause(ShipmentEvent event) {
        if (event.eventType() == EventType.DELIVERED) {
            return "Shipment delivered; active risk is closed.";
        }
        if (event.exceptionCode() == null) {
            return "No hard exception detected; risk is driven by ETA variance and dwell time.";
        }
        return switch (event.exceptionCode()) {
            case PORT_CONGESTION -> "Port congestion is increasing dwell time and berth uncertainty.";
            case WEATHER_RISK -> "Weather exposure is increasing transit variability on the current lane.";
            case CUSTOMS_HOLD -> "Customs hold is blocking downstream handoff and delivery appointment planning.";
            case CAPACITY_SHORTAGE -> "Carrier capacity shortage is constraining planned movement.";
            case TEMPERATURE_EXCURSION -> "Temperature excursion threatens cargo quality and may require inspection.";
            case ROUTE_DEVIATION -> "Route deviation indicates a mismatch between planned and observed movement.";
            case MISSED_CONNECTION -> "Missed intermodal connection is creating schedule recovery pressure.";
            case UNKNOWN -> "Exception details are incomplete; operations should enrich the case notes.";
        };
    }

    private String recommendation(ShipmentEvent event, double score, long projectedDelay) {
        if (event.eventType() == EventType.DELIVERED) {
            return "Close monitoring case and capture final transit variance for carrier scorecards.";
        }
        if (score >= 0.75) {
            return "Escalate to control tower, notify customer success, and book an alternate recovery option.";
        }
        if (score >= 0.45 || projectedDelay > 240) {
            return "Request carrier recovery plan, validate next appointment, and prepare customer ETA update.";
        }
        return "Continue automated monitoring and refresh ETA after the next location event.";
    }
}
