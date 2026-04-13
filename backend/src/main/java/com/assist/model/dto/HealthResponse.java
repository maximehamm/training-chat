package com.assist.model.dto;

public record HealthResponse(String status, String version, long uptime, String reason) {

    public HealthResponse(String status, String version, long uptime) {
        this(status, version, uptime, null);
    }
}
