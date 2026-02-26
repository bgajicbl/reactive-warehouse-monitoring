package com.warehouse.monitoring.domain.model;

import lombok.Getter;

@Getter
public enum SensorType {
  TEMPERATURE("TEMPERATURE"),
  HUMIDITY("HUMIDITY");

  private final String jsonTypeName;

  SensorType(String jsonTypeName) {
    this.jsonTypeName = jsonTypeName;
  }

  @Override
  public String toString() {
    return this.name();
  }
}
