package com.autonomous.supplychain.intelligence.copilot;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class LogisticsQueryService {
    private static final String HIGH_RISK_SQL = """
            SELECT shipment_id, order_number, carrier, origin, destination, last_location_name,
                   status, exception_code, risk_score, delay_minutes, root_cause, recommendation
            FROM shipment_risk_profiles
            WHERE risk_score >= 0.45
            ORDER BY risk_score DESC
            LIMIT 10
            """;

    private static final String BOTTLENECK_SQL = """
            SELECT location_name, exception_code, severity, shipments_impacted, average_dwell_minutes, updated_at
            FROM bottleneck_observations
            ORDER BY shipments_impacted DESC, updated_at DESC
            LIMIT 10
            """;

    private static final String ROOT_CAUSE_SQL = """
            SELECT shipment_id, order_number, carrier, exception_code, risk_score, delay_minutes,
                   root_cause, recommendation, updated_at
            FROM shipment_risk_profiles
            ORDER BY risk_score DESC, updated_at DESC
            LIMIT 10
            """;

    private static final String SUMMARY_SQL = """
            SELECT status, COUNT(*) AS shipment_count, ROUND(AVG(COALESCE(risk_score, 0))::numeric, 2) AS average_risk
            FROM shipment_risk_profiles
            GROUP BY status
            ORDER BY shipment_count DESC
            LIMIT 10
            """;

    private final JdbcTemplate jdbcTemplate;

    public LogisticsQueryService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public QueryPlan planAndExecute(String question) {
        QueryIntent intent = resolveIntent(question);
        String sql = sqlFor(intent);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return new QueryPlan(intent, compact(sql), rows);
    }

    QueryIntent resolveIntent(String question) {
        String normalized = question == null ? "" : question.toLowerCase(Locale.ROOT);
        if (normalized.contains("bottleneck") || normalized.contains("congestion") || normalized.contains("dwell")) {
            return QueryIntent.BOTTLENECK;
        }
        if (normalized.contains("why") || normalized.contains("root cause") || normalized.contains("cause")) {
            return QueryIntent.ROOT_CAUSE;
        }
        if (normalized.contains("delay") || normalized.contains("risk") || normalized.contains("late")) {
            return QueryIntent.DELAY_RISK;
        }
        return QueryIntent.SUMMARY;
    }

    private String sqlFor(QueryIntent intent) {
        return switch (intent) {
            case BOTTLENECK -> BOTTLENECK_SQL;
            case ROOT_CAUSE -> ROOT_CAUSE_SQL;
            case SUMMARY -> SUMMARY_SQL;
            case DELAY_RISK -> HIGH_RISK_SQL;
        };
    }

    private String compact(String sql) {
        return sql.replaceAll("\\s+", " ").trim();
    }
}
