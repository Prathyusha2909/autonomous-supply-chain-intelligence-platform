package com.autonomous.supplychain.intelligence.api;

import com.autonomous.supplychain.intelligence.copilot.CopilotAnswer;
import com.autonomous.supplychain.intelligence.copilot.CopilotQueryRequest;
import com.autonomous.supplychain.intelligence.copilot.CopilotService;
import com.autonomous.supplychain.intelligence.repository.BottleneckObservationRepository;
import com.autonomous.supplychain.intelligence.repository.ShipmentRiskProfileRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IntelligenceController {
    private final ShipmentRiskProfileRepository riskProfileRepository;
    private final BottleneckObservationRepository bottleneckRepository;
    private final CopilotService copilotService;

    public IntelligenceController(
            ShipmentRiskProfileRepository riskProfileRepository,
            BottleneckObservationRepository bottleneckRepository,
            CopilotService copilotService
    ) {
        this.riskProfileRepository = riskProfileRepository;
        this.bottleneckRepository = bottleneckRepository;
        this.copilotService = copilotService;
    }

    @GetMapping("/intelligence/summary")
    OperationsSummary summary() {
        List<RiskProfileResponse> profiles = riskProfileRepository.findTop20ByOrderByRiskScoreDesc()
                .stream()
                .map(RiskProfileResponse::from)
                .toList();
        long tracked = riskProfileRepository.count();
        long highRisk = profiles.stream().filter(profile -> profile.riskScore() != null && profile.riskScore() >= 0.65).count();
        double averageRisk = profiles.stream()
                .map(RiskProfileResponse::riskScore)
                .filter(score -> score != null)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        return new OperationsSummary(tracked, highRisk, bottleneckRepository.count(), averageRisk);
    }

    @GetMapping("/intelligence/risks")
    List<RiskProfileResponse> risks(@RequestParam(name = "threshold", defaultValue = "0.0") double threshold) {
        return riskProfileRepository.findByRiskScoreGreaterThanEqualOrderByRiskScoreDesc(threshold)
                .stream()
                .map(RiskProfileResponse::from)
                .toList();
    }

    @GetMapping("/intelligence/shipments/{shipmentId}")
    RiskProfileResponse shipmentRisk(@PathVariable("shipmentId") String shipmentId) {
        return riskProfileRepository.findById(shipmentId)
                .map(RiskProfileResponse::from)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Risk profile not found"));
    }

    @GetMapping("/intelligence/bottlenecks")
    List<BottleneckResponse> bottlenecks() {
        return bottleneckRepository.findTop20ByOrderByShipmentsImpactedDescUpdatedAtDesc()
                .stream()
                .map(BottleneckResponse::from)
                .toList();
    }

    @PostMapping("/copilot/query")
    CopilotAnswer askCopilot(@Valid @RequestBody CopilotQueryRequest request) {
        return copilotService.answer(request.question());
    }
}
