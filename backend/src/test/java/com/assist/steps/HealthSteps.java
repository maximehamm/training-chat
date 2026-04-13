package com.assist.steps;

import io.cucumber.java.en.When;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.web.client.TestRestTemplate;

@RequiredArgsConstructor
public class HealthSteps {

    private final TestRestTemplate restTemplate;
    private final HttpContext httpContext;

    @When("je GET /health")
    public void jeGetHealth() {
        httpContext.setLastResponse(restTemplate.getForEntity("/health", String.class));
    }
}
