package com.autonomous.supplychain.intelligence.copilot;

import java.util.Optional;

public interface AiCopilotClient {
    boolean isConfigured();

    Optional<CopilotAnswer> answer(String question, QueryPlan queryPlan, CopilotAnswer fallback);
}
