package com.assist.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ClaudeConfig {

  @Value("${anthropic.api-key:}")
  private String apiKey;

  @Value("${claude.model:claude-sonnet-4-5-20250514}")
  private String defaultModel;

  @Value("${claude.max-tokens:4096}")
  private int defaultMaxTokens;

  @Value("${claude.temperature:0.7}")
  private double defaultTemperature;

  public boolean isApiKeyPresent() {
    return apiKey != null && !apiKey.isBlank();
  }
}
