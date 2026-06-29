package com.autonomous.supplychain.intelligence.copilot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ClaudeCopilotClient implements AiCopilotClient {
    private static final Logger log = LoggerFactory.getLogger(ClaudeCopilotClient.class);

    private final RestClient restClient;
    private final CopilotProperties properties;

    public ClaudeCopilotClient(RestClient.Builder restClientBuilder, CopilotProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    @Override
    public boolean isConfigured() {
        return properties.isAnthropicEnabled();
    }

    @Override
    public Optional<CopilotAnswer> answer(String question, QueryPlan queryPlan, CopilotAnswer fallback) {
        if (!isConfigured()) {
            return Optional.empty();
        }
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", properties.getAnthropic().getModel());
            payload.put("max_tokens", properties.getAnthropic().getMaxTokens());
            payload.put("system", systemPrompt());
            payload.put("messages", List.of(Map.of("role", "user", "content", userPrompt(question, queryPlan))));

            Map<String, Object> response = restClient.post()
                    .uri(properties.getAnthropic().getEndpoint())
                    .header("x-api-key", properties.getAnthropic().getApiKey())
                    .header("anthropic-version", properties.getAnthropic().getVersion())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            return extractText(response)
                    .map(text -> withProviderEvidence(text, fallback, queryPlan, "Claude " + properties.getAnthropic().getModel()));
        } catch (RestClientException ex) {
            log.warn("Claude copilot request failed; falling back to deterministic response", ex);
            return Optional.empty();
        }
    }

    private String systemPrompt() {
        return """
                You are a logistics operations copilot for a supply chain control tower.
                Use only the provided SQL result rows. Keep the answer operational, concise, and action-oriented.
                Do not invent shipment IDs, carriers, locations, or metrics.
                """;
    }

    private String userPrompt(String question, QueryPlan queryPlan) {
        return """
                Question: %s

                Query intent: %s
                SQL executed: %s
                Rows:
                %s
                """.formatted(question, queryPlan.intent(), queryPlan.sql(), queryPlan.rowsAsText());
    }

    private Optional<String> extractText(Map<String, Object> response) {
        if (response == null) {
            return Optional.empty();
        }
        Object content = response.get("content");
        if (content instanceof List<?> contentItems) {
            for (Object contentItem : contentItems) {
                if (contentItem instanceof Map<?, ?> contentMap) {
                    Object text = contentMap.get("text");
                    if (text instanceof String value && !value.isBlank()) {
                        return Optional.of(value.trim());
                    }
                }
            }
        }
        return Optional.empty();
    }

    private CopilotAnswer withProviderEvidence(String answer, CopilotAnswer fallback, QueryPlan queryPlan, String provider) {
        List<CopilotEvidence> evidence = new ArrayList<>();
        evidence.add(new CopilotEvidence("ai-provider", provider, "LLM-generated answer over a safe predefined SQL query."));
        evidence.add(new CopilotEvidence("query-plan", queryPlan.intent().name(), queryPlan.sql()));
        evidence.addAll(fallback.evidence());
        return new CopilotAnswer(answer, fallback.recommendations(), evidence, Instant.now());
    }
}
