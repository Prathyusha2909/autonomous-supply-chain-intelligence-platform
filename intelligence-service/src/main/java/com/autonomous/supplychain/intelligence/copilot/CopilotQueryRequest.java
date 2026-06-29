package com.autonomous.supplychain.intelligence.copilot;

import jakarta.validation.constraints.NotBlank;

public record CopilotQueryRequest(@NotBlank String question) {
}
