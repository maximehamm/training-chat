package com.assist.steps;

import static org.assertj.core.api.Assertions.assertThat;

import com.assist.config.ClaudeConfig;
import com.assist.model.entity.AssistConfig;
import com.assist.repository.AssistConfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RequiredArgsConstructor
public class ConfigSteps {

  private final TestRestTemplate restTemplate;
  private final HttpContext httpContext;
  private final AssistConfigRepository configRepository;
  private final ClaudeConfig claudeConfig;
  private final ObjectMapper objectMapper;

  @Given("la configuration est réinitialisée aux valeurs par défaut")
  public void laConfigurationEstReinitialisee() {
    AssistConfig config = configRepository.findById(1L).orElseThrow();
    config.setModel(claudeConfig.getDefaultModel());
    config.setMaxTokens(claudeConfig.getDefaultMaxTokens());
    config.setTemperature(claudeConfig.getDefaultTemperature());
    configRepository.save(config);
  }

  @When("^je GET /config$")
  public void jeGetConfig() {
    httpContext.setLastResponse(restTemplate.getForEntity("/config", String.class));
  }

  @When("^je POST /config avec le body \"(.+)\"$")
  public void jePostConfigAvecLeBody(String body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    httpContext.setLastResponse(
        restTemplate.postForEntity("/config", new HttpEntity<>(body, headers), String.class));
  }

  @Then("la base contient le modèle {string}")
  public void laBaseContientLeModele(String model) {
    AssistConfig config = configRepository.findById(1L).orElseThrow();
    assertThat(config.getModel()).isEqualTo(model);
  }

  @Then("la base contient max_tokens {int}")
  public void laBaseContientMaxTokens(int maxTokens) {
    AssistConfig config = configRepository.findById(1L).orElseThrow();
    assertThat(config.getMaxTokens()).isEqualTo(maxTokens);
  }

  @Then("la réponse contient une erreur {string}")
  public void laReponseContientUneErreur(String errorCode) throws Exception {
    JsonNode body = objectMapper.readTree(httpContext.getLastResponse().getBody());
    assertThat(body.get("error").asText()).isEqualTo(errorCode);
  }
}
