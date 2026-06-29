package com.autonomous.supplychain.intelligence.copilot;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LogisticsQueryServiceTest {
    private final LogisticsQueryService queryService = new LogisticsQueryService(mock(org.springframework.jdbc.core.JdbcTemplate.class));

    @Test
    void mapsDelayQuestionToRiskQuery() {
        assertThat(queryService.resolveIntent("Which shipments are likely delayed?"))
                .isEqualTo(QueryIntent.DELAY_RISK);
    }

    @Test
    void mapsCongestionQuestionToBottleneckQuery() {
        assertThat(queryService.resolveIntent("Where is port congestion creating bottlenecks?"))
                .isEqualTo(QueryIntent.BOTTLENECK);
    }

    @Test
    void mapsWhyQuestionToRootCauseQuery() {
        assertThat(queryService.resolveIntent("Why is the Maersk shipment late?"))
                .isEqualTo(QueryIntent.ROOT_CAUSE);
    }
}
