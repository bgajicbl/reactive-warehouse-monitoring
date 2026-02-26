package com.monitoring.central.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.warehouse.monitoring.domain.model.Alarm;
import com.warehouse.monitoring.domain.model.SensorType;
import com.warehouse.monitoring.domain.model.TemperatureMeasurement;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class ThresholdMonitorTest {

  private ThresholdMonitor thresholdMonitor;

  @BeforeEach
  void setUp() {
    thresholdMonitor = new ThresholdMonitor();
    ReflectionTestUtils.setField(thresholdMonitor, "temperatureThreshold", 35.0);
    ReflectionTestUtils.setField(thresholdMonitor, "humidityThreshold", 50.0);
  }

  @Test
  void shouldGenerateAlarmWhenTemperatureExceedsThreshold() {
    // Given
    TemperatureMeasurement measurement = new TemperatureMeasurement();
    measurement.setSensorId("temp-001");
    measurement.setValue(40.0);
    measurement.setTimestamp(Instant.now());
    measurement.setWarehouseId("warehouse-1");

    // When
    Mono<Alarm> result = thresholdMonitor.checkThreshold(measurement);

    // Then
    StepVerifier.create(result)
        .assertNext(
            alarm -> {
              assertThat(alarm.getSensorId()).isEqualTo("temp-001");
              assertThat(alarm.getSensorType()).isEqualTo(SensorType.TEMPERATURE);
              assertThat(alarm.getValue()).isEqualTo(40.0);
              assertThat(alarm.getThreshold()).isEqualTo(35.0);
              assertThat(alarm.getWarehouseId()).isEqualTo("warehouse-1");
              assertThat(alarm.getTimestamp()).isNotNull();
            })
        .verifyComplete();
  }
}
