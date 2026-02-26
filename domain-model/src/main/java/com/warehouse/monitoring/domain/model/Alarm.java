package com.warehouse.monitoring.domain.model;

import java.time.Instant;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Alarm {
  private String sensorId;
  private SensorType sensorType;
  private double value;
  private double threshold;
  private Instant timestamp = Instant.now();
  private String warehouseId;

  public Alarm(
      String sensorId, SensorType sensorType, double value, double threshold, String warehouseId) {
    this.sensorId = sensorId;
    this.sensorType = sensorType;
    this.value = value;
    this.threshold = threshold;
    this.timestamp = Instant.now();
    this.warehouseId = warehouseId;
  }
}
