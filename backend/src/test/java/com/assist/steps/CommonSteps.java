package com.assist.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.Then;
import lombok.RequiredArgsConstructor;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class CommonSteps {

    private final HttpContext httpContext;
    private final ObjectMapper objectMapper;

    @Then("le statut HTTP est {int}")
    public void leStatutHttpEst(int status) {
        assertThat(httpContext.getLastResponse().getStatusCode().value()).isEqualTo(status);
    }

    @Then("la réponse contient le champ {string} avec la valeur {string}")
    public void laReponseContientLeChampAvecLaValeur(String field, String value) throws Exception {
        JsonNode body = objectMapper.readTree(httpContext.getLastResponse().getBody());
        assertThat(body.has(field)).as("Champ '%s' absent de la réponse", field).isTrue();
        assertThat(body.get(field).asText()).isEqualTo(value);
    }

    @Then("la réponse contient le champ {string} non nul")
    public void laReponseContientLeChampNonNul(String field) throws Exception {
        JsonNode body = objectMapper.readTree(httpContext.getLastResponse().getBody());
        assertThat(body.has(field)).as("Champ '%s' absent de la réponse", field).isTrue();
        assertThat(body.get(field).isNull()).isFalse();
    }
}
