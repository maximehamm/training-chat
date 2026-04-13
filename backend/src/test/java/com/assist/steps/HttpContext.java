package com.assist.steps;

import io.cucumber.spring.ScenarioScope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class HttpContext {

    private ResponseEntity<String> lastResponse;

    public void setLastResponse(ResponseEntity<String> lastResponse) {
        this.lastResponse = lastResponse;
    }

    public ResponseEntity<String> getLastResponse() {
        return lastResponse;
    }
}
