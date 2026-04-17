package com.assist.controller;

import com.assist.model.dto.BonjourResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BonjourController {

  private static final Logger log = LoggerFactory.getLogger(BonjourController.class);
  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

  @GetMapping("/bonjour")
  public BonjourResponse bonjour() {
    log.info("[API] GET /bonjour invoked");
    BonjourResponse response =
        new BonjourResponse("Bonjour Maxime il est " + LocalTime.now().format(TIME_FORMATTER));
    log.info("[API] GET /bonjour response: {}", response);
    return response;
  }
}
