package com.warehouse.monitoring.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("HUMIDITY")
@NoArgsConstructor
@ToString
public class HumidityMeasurement extends SensorMeasurement {

  @Override
  public SensorType getSensorType() {
    return SensorType.HUMIDITY;
  }
}
