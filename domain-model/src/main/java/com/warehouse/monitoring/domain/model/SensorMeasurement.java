package com.warehouse.monitoring.domain.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = TemperatureMeasurement.class, name = "TEMPERATURE"),
  @JsonSubTypes.Type(value = HumidityMeasurement.class, name = "HUMIDITY")
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SensorMeasurement {

  private String sensorId;
  private double value;
  private Instant timestamp;
  private String warehouseId;

  protected SensorMeasurement(String sensorId, double value, String warehouseId) {
    this.sensorId = sensorId;
    this.value = value;
    this.timestamp = Instant.now();
    this.warehouseId = warehouseId;
  }

  public abstract SensorType getSensorType();
}
