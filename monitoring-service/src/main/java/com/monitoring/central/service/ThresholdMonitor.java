package com.monitoring.central.service;

import com.warehouse.monitoring.domain.model.Alarm;
import com.warehouse.monitoring.domain.model.SensorMeasurement;
import com.warehouse.monitoring.domain.model.SensorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class ThresholdMonitor {

  @Value("${monitoring.thresholds.temperature:35.0}")
  private double temperatureThreshold;

  @Value("${monitoring.thresholds.humidity:50.0}")
  private double humidityThreshold;

  public Mono<Alarm> checkThreshold(SensorMeasurement measurement) {
    return Mono.fromCallable(
        () -> {
          SensorType type = measurement.getSensorType();
          double value = measurement.getValue();
          double threshold = getThreshold(type);

          if (value > threshold) {
            log.debug(
                "Threshold exceeded: type={}, value={}, threshold={}", type, value, threshold);

            return new Alarm(
                measurement.getSensorId(), type, value, threshold, measurement.getWarehouseId());
          }

          return null;
        });
  }

  private double getThreshold(SensorType type) {
    return switch (type) {
      case TEMPERATURE -> temperatureThreshold;
      case HUMIDITY -> humidityThreshold;
    };
  }
}
