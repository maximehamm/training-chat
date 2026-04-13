package com.assist.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "assist_config")
@Getter
@Setter
@NoArgsConstructor
public class AssistConfig {

  @Id private Long id = 1L;

  @Column(nullable = false)
  private String model;

  @Column(nullable = false)
  private Integer maxTokens;

  @Column(nullable = false)
  private Double temperature;

  public AssistConfig(String model, Integer maxTokens, Double temperature) {
    this.model = model;
    this.maxTokens = maxTokens;
    this.temperature = temperature;
  }
}
