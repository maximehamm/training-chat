package com.assist.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ConfigResponse(
    String model, @JsonProperty("max_tokens") int maxTokens, double temperature) {}
