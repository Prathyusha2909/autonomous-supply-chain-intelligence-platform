package com.autonomous.supplychain.intelligence.api;

public record OperationsSummary(
        long trackedShipments,
        long highRiskShipments,
        long activeBottlenecks,
        double averageRiskScore
) {
}
