package com.autonomous.supplychain.intelligence.copilot;

import com.autonomous.supplychain.intelligence.domain.BottleneckObservation;
import com.autonomous.supplychain.intelligence.domain.ShipmentRiskProfile;
import com.autonomous.supplychain.intelligence.repository.BottleneckObservationRepository;
import com.autonomous.supplychain.intelligence.repository.ShipmentRiskProfileRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CopilotService {
    private final ShipmentRiskProfileRepository riskProfileRepository;
    private final BottleneckObservationRepository bottleneckRepository;
    private final LogisticsQueryService logisticsQueryService;
    private final List<AiCopilotClient> aiCopilotClients;

    public CopilotService(
            ShipmentRiskProfileRepository riskProfileRepository,
            BottleneckObservationRepository bottleneckRepository,
            LogisticsQueryService logisticsQueryService,
            List<AiCopilotClient> aiCopilotClients
    ) {
        this.riskProfileRepository = riskProfileRepository;
        this.bottleneckRepository = bottleneckRepository;
        this.logisticsQueryService = logisticsQueryService;
        this.aiCopilotClients = aiCopilotClients;
    }

    public CopilotAnswer answer(String question) {
        QueryPlan queryPlan = logisticsQueryService.planAndExecute(question);
        String normalized = question.toLowerCase();
        CopilotAnswer fallback;
        if (normalized.contains("bottleneck") || normalized.contains("congestion")) {
            fallback = bottleneckAnswer();
        } else if (normalized.contains("why") || normalized.contains("root cause")) {
            fallback = rootCauseAnswer();
        } else if (normalized.contains("delay") || normalized.contains("risk") || normalized.contains("late")) {
            fallback = delayRiskAnswer();
        } else {
            fallback = operationsSummaryAnswer();
        }
        return configuredAiClient()
                .flatMap(client -> client.answer(question, queryPlan, fallback))
                .orElseGet(() -> withQueryEvidence(fallback, queryPlan));
    }

    private Optional<AiCopilotClient> configuredAiClient() {
        return aiCopilotClients.stream()
                .filter(AiCopilotClient::isConfigured)
                .findFirst();
    }

    private CopilotAnswer withQueryEvidence(CopilotAnswer answer, QueryPlan queryPlan) {
        List<CopilotEvidence> evidence = new ArrayList<>();
        evidence.add(new CopilotEvidence("query-plan", queryPlan.intent().name(), queryPlan.sql()));
        evidence.addAll(answer.evidence());
        return new CopilotAnswer(answer.answer(), answer.recommendations(), evidence, answer.generatedAt());
    }

    private CopilotAnswer delayRiskAnswer() {
        List<ShipmentRiskProfile> risky = riskProfileRepository.findByRiskScoreGreaterThanEqualOrderByRiskScoreDesc(0.45);
        List<CopilotEvidence> evidence = risky.stream()
                .limit(5)
                .map(profile -> new CopilotEvidence(
                        "shipment",
                        profile.getShipmentId() + " risk " + percent(profile.getRiskScore()),
                        nullSafe(profile.getRootCause())
                ))
                .toList();
        List<String> recommendations = risky.stream()
                .limit(3)
                .map(ShipmentRiskProfile::getRecommendation)
                .distinct()
                .toList();
        String answer = risky.isEmpty()
                ? "No shipment currently exceeds the delay risk threshold. Continue monitoring live ETA and exception events."
                : risky.size() + " shipment(s) are above the delay risk threshold. The highest-risk lanes need carrier recovery confirmation and proactive customer ETA updates.";
        return new CopilotAnswer(answer, recommendations, evidence, Instant.now());
    }

    private CopilotAnswer bottleneckAnswer() {
        List<BottleneckObservation> bottlenecks = bottleneckRepository.findTop20ByOrderByShipmentsImpactedDescUpdatedAtDesc();
        List<CopilotEvidence> evidence = bottlenecks.stream()
                .limit(5)
                .map(observation -> new CopilotEvidence(
                        "bottleneck",
                        observation.getLocationName(),
                        observation.getShipmentsImpacted() + " impacted shipment(s), average dwell "
                                + Math.round(observation.getAverageDwellMinutes()) + " minutes"
                ))
                .toList();
        List<String> recommendations = bottlenecks.isEmpty()
                ? List.of("Keep watching exception velocity by port, warehouse, and carrier handoff.")
                : List.of("Prioritize alternate routing for the largest bottleneck.", "Ask the carrier for recovery slots and revised cutoffs.");
        String answer = bottlenecks.isEmpty()
                ? "No active bottleneck has enough signal yet."
                : "The top bottleneck is " + bottlenecks.get(0).getLocationName() + ", driven by "
                + bottlenecks.get(0).getExceptionCode() + ".";
        return new CopilotAnswer(answer, recommendations, evidence, Instant.now());
    }

    private CopilotAnswer rootCauseAnswer() {
        List<ShipmentRiskProfile> profiles = riskProfileRepository.findTop20ByOrderByRiskScoreDesc();
        List<CopilotEvidence> evidence = profiles.stream()
                .limit(5)
                .map(profile -> new CopilotEvidence(
                        "root-cause",
                        profile.getShipmentId(),
                        nullSafe(profile.getRootCause())
                ))
                .toList();
        String answer = profiles.isEmpty()
                ? "There are no processed shipment events yet, so root-cause analysis is waiting on telemetry."
                : "The leading root cause is: " + nullSafe(profiles.get(0).getRootCause());
        List<String> recommendations = profiles.stream()
                .limit(3)
                .map(ShipmentRiskProfile::getRecommendation)
                .distinct()
                .toList();
        return new CopilotAnswer(answer, recommendations, evidence, Instant.now());
    }

    private CopilotAnswer operationsSummaryAnswer() {
        List<ShipmentRiskProfile> recent = riskProfileRepository.findTop10ByOrderByUpdatedAtDesc();
        List<CopilotEvidence> evidence = new ArrayList<>();
        for (ShipmentRiskProfile profile : recent) {
            evidence.add(new CopilotEvidence("shipment", profile.getShipmentId(), "Last event " + profile.getLastEventType()));
        }
        return new CopilotAnswer(
                "The control tower is tracking " + riskProfileRepository.count()
                        + " shipment(s). Ask about delay risk, bottlenecks, or root cause for a deeper operational view.",
                List.of("Review high-risk shipments first.", "Confirm carrier recovery plans for open exceptions."),
                evidence,
                Instant.now()
        );
    }

    private String nullSafe(String value) {
        return value == null || value.isBlank() ? "No explanation available yet." : value;
    }

    private String percent(Double value) {
        return Math.round((value == null ? 0.0 : value) * 100) + "%";
    }
}
