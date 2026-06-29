package com.autonomous.supplychain.intelligence.copilot;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record QueryPlan(
        QueryIntent intent,
        String sql,
        List<Map<String, Object>> rows
) {
    public String rowsAsText() {
        if (rows.isEmpty()) {
            return "No rows returned.";
        }
        return rows.stream()
                .map(row -> row.entrySet().stream()
                        .map(entry -> entry.getKey() + "=" + entry.getValue())
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("\n"));
    }
}
