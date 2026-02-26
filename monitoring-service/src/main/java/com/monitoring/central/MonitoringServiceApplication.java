package com.monitoring.central;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication(
    scanBasePackages = {"com.monitoring.central", "com.warehouse.monitoring.domain"})
@ConfigurationPropertiesScan
@Slf4j
public class MonitoringServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(MonitoringServiceApplication.class, args);
    String separator = "=".repeat(40);
    log.info("{}", separator);
    log.info("Central Monitoring Service Started");
    log.info("Listening for sensor threshold violations...");
    log.info("{}", separator);
  }
}
