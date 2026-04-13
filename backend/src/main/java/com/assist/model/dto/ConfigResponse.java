package com.assist.model.dto;

public record ConfigResponse(String model, int maxTokens, double temperature) {
}
