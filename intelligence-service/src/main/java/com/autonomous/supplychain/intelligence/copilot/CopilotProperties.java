package com.autonomous.supplychain.intelligence.copilot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.ai")
public class CopilotProperties {
    private String provider = "none";
    private OpenAi openai = new OpenAi();
    private Anthropic anthropic = new Anthropic();

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public OpenAi getOpenai() {
        return openai;
    }

    public void setOpenai(OpenAi openai) {
        this.openai = openai;
    }

    public Anthropic getAnthropic() {
        return anthropic;
    }

    public void setAnthropic(Anthropic anthropic) {
        this.anthropic = anthropic;
    }

    public boolean isOpenAiEnabled() {
        return "openai".equalsIgnoreCase(provider) && hasText(openai.apiKey);
    }

    public boolean isAnthropicEnabled() {
        return "anthropic".equalsIgnoreCase(provider) && hasText(anthropic.apiKey);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public static class OpenAi {
        private String apiKey;
        private String endpoint = "https://api.openai.com/v1/responses";
        private String model = "gpt-5.5";
        private int maxOutputTokens = 700;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public int getMaxOutputTokens() {
            return maxOutputTokens;
        }

        public void setMaxOutputTokens(int maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
        }
    }

    public static class Anthropic {
        private String apiKey;
        private String endpoint = "https://api.anthropic.com/v1/messages";
        private String model = "claude-sonnet-4-5";
        private String version = "2023-06-01";
        private int maxTokens = 700;

        public String getApiKey() {
            return apiKey;
        }

        public void setApiKey(String apiKey) {
            this.apiKey = apiKey;
        }

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public void setMaxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
        }
    }
}
