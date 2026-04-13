package com.assist.service;

import com.assist.config.ClaudeConfig;
import com.assist.model.dto.ConfigRequest;
import com.assist.model.dto.ConfigResponse;
import com.assist.model.entity.AssistConfig;
import com.assist.repository.AssistConfigRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssistConfigService {

    private final AssistConfigRepository configRepository;
    private final ClaudeConfig claudeConfig;

    @PostConstruct
    public void init() {
        if (!configRepository.existsById(1L)) {
            configRepository.save(new AssistConfig(
                claudeConfig.getDefaultModel(),
                claudeConfig.getDefaultMaxTokens(),
                claudeConfig.getDefaultTemperature()
            ));
        }
    }

    @Transactional(readOnly = true)
    public ConfigResponse getConfig() {
        AssistConfig config = configRepository.findById(1L).orElseThrow();
        return new ConfigResponse(config.getModel(), config.getMaxTokens(), config.getTemperature());
    }

    @Transactional
    public ConfigResponse updateConfig(ConfigRequest request) {
        AssistConfig config = configRepository.findById(1L).orElseThrow();
        if (request.model() != null) {
            config.setModel(request.model());
        }
        if (request.maxTokens() != null) {
            config.setMaxTokens(request.maxTokens());
        }
        if (request.temperature() != null) {
            config.setTemperature(request.temperature());
        }
        return new ConfigResponse(config.getModel(), config.getMaxTokens(), config.getTemperature());
    }
}
