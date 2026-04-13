package com.assist.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

public record ConfigRequest(
    @Pattern(
            regexp = "claude-[a-z0-9\\-]+",
            message = "Modèle invalide — doit commencer par 'claude-'")
        String model,
    @JsonProperty("max_tokens")
        @Min(value = 1, message = "max_tokens doit être au moins 1")
        @Max(value = 8192, message = "max_tokens ne peut pas dépasser 8192")
        Integer maxTokens,
    @DecimalMin(value = "0.0", message = "temperature doit être entre 0.0 et 1.0")
        @DecimalMax(value = "1.0", message = "temperature doit être entre 0.0 et 1.0")
        Double temperature) {}
