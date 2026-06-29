package com.autonomous.supplychain.intelligence.service;

public record RiskAssessment(
        double riskScore,
        long delayMinutes,
        String rootCause,
        String recommendation
) {
}
