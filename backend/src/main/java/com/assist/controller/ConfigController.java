package com.assist.controller;

import com.assist.model.dto.ConfigRequest;
import com.assist.model.dto.ConfigResponse;
import com.assist.service.AssistConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
@Validated
@RequiredArgsConstructor
public class ConfigController {

  private final AssistConfigService configService;

  @GetMapping
  public ConfigResponse getConfig() {
    return configService.getConfig();
  }

  @PostMapping
  public ConfigResponse updateConfig(@Valid @RequestBody ConfigRequest request) {
    return configService.updateConfig(request);
  }
}
