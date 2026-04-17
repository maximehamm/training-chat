package com.assist.mcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class BonjourTool {

  private static final Logger log = LoggerFactory.getLogger(BonjourTool.class);

  private final RestClient restClient;

  public BonjourTool(
      RestClient.Builder builder,
      @Value("${assist.backend.url:http://localhost:8080}") String baseUrl) {
    this.restClient = builder.baseUrl(baseUrl).build();
  }

  @Tool(
      name = "bonjour-claude",
      description =
          "Appelle l'API Assist GET /bonjour et retourne le message de bienvenue avec l'heure courante.")
  public String bonjourClaude() {
    log.info("[MCP tool] bonjour-claude invoked");
    String response = restClient.get().uri("/bonjour").retrieve().body(String.class);
    log.info("[MCP tool] bonjour-claude response: {}", response);
    return response;
  }
}
