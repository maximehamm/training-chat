package com.assist.controller;

import com.assist.config.ClaudeConfig;
import com.assist.model.dto.HealthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final ClaudeConfig claudeConfig;

    @Value("${assist.version:1.0.0}")
    private String version;

    private final long startTime = System.currentTimeMillis();

    @GetMapping("/health")
    public HealthResponse health() {
        long uptime = (System.currentTimeMillis() - startTime) / 1000;
        if (!claudeConfig.isApiKeyPresent()) {
            return new HealthResponse("degraded", version, uptime, "missing_api_key");
        }
        return new HealthResponse("ok", version, uptime);
    }
}
