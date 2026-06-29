package com.autonomous.supplychain.intelligence.copilot;

import java.time.Instant;
import java.util.List;

public record CopilotAnswer(
        String answer,
        List<String> recommendations,
        List<CopilotEvidence> evidence,
        Instant generatedAt
) {
}
