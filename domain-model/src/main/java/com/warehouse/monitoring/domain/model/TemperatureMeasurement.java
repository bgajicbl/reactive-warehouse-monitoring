package com.warehouse.monitoring.domain.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.NoArgsConstructor;
import lombok.ToString;

@JsonTypeName("TEMPERATURE")
@NoArgsConstructor
@ToString
public class TemperatureMeasurement extends SensorMeasurement {

  @Override
  public SensorType getSensorType() {
    return SensorType.TEMPERATURE;
  }
}
