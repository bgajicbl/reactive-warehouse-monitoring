package com.warehouse.monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class WarehouseServiceApplication {

  static void main(String[] args) {
    SpringApplication.run(WarehouseServiceApplication.class, args);
  }
}
