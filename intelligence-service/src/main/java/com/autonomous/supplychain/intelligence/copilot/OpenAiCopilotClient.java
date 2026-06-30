package com.autonomous.supplychain.intelligence.copilot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
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
public class OpenAiCopilotClient implements AiCopilotClient {
    private static final Logger log = LoggerFactory.getLogger(OpenAiCopilotClient.class);

    private final RestClient restClient;
    private final CopilotProperties properties;

    public OpenAiCopilotClient(RestClient.Builder restClientBuilder, CopilotProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    @Override
    public boolean isConfigured() {
        return properties.isOpenAiEnabled();
    }

    @Override
    public Optional<CopilotAnswer> answer(String question, QueryPlan queryPlan, CopilotAnswer fallback) {
        if (!isConfigured()) {
            return Optional.empty();
        }
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", properties.getOpenai().getModel());
            payload.put("max_output_tokens", properties.getOpenai().getMaxOutputTokens());
            payload.put("input", List.of(
                    Map.of("role", "developer", "content", systemPrompt()),
                    Map.of("role", "user", "content", userPrompt(question, queryPlan))
            ));

            Map<String, Object> response = restClient.post()
                    .uri(properties.getOpenai().getEndpoint())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getOpenai().getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            return extractText(response)
                    .map(text -> withProviderEvidence(text, fallback, queryPlan, "OpenAI " + properties.getOpenai().getModel()));
        } catch (RestClientException ex) {
            log.warn("OpenAI copilot request failed; using offline fallback response", ex);
            return Optional.empty();
        }
    }

    private String systemPrompt() {
        return """
                You are a logistics operations copilot for a supply chain control tower.
                Answer using only the provided SQL result rows and operational context.
                Be concise, name the highest-risk shipment or bottleneck, and include concrete next actions.
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
        Object outputText = response.get("output_text");
        if (outputText instanceof String text && !text.isBlank()) {
            return Optional.of(text.trim());
        }
        Object output = response.get("output");
        if (output instanceof List<?> outputItems) {
            for (Object outputItem : outputItems) {
                if (outputItem instanceof Map<?, ?> outputMap) {
                    Object content = outputMap.get("content");
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
