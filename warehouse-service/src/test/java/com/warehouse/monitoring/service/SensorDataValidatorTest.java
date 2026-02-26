package com.warehouse.monitoring.service;

import com.warehouse.monitoring.config.WarehouseProperties;
import com.warehouse.monitoring.domain.model.TemperatureMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SensorDataValidatorTest {

    private SensorDataValidator validator;
    private WarehouseProperties.LocationConfig locationConfig;

    @BeforeEach
    void setUp() {
        validator = new SensorDataValidator();
        WarehouseProperties properties = new WarehouseProperties();
        locationConfig = new WarehouseProperties.LocationConfig();
        locationConfig.setId("warehouse-1");
        properties.getLocations().put("location-1", locationConfig);
    }

    @Test
    void shouldValidateValidTemperatureMeasurement() {
        // Given
        TemperatureMeasurement measurement = new TemperatureMeasurement();
        measurement.setSensorId("temp-001");
        measurement.setValue(25.0);
        measurement.setTimestamp(Instant.now());
        measurement.setWarehouseId("warehouse-1");

        // When
        Mono<TemperatureMeasurement> result = validator.validate(measurement, locationConfig);

        // Then
        StepVerifier.create(result)
                .assertNext(validated -> {
                    assertThat(validated.getSensorId()).isEqualTo("temp-001");
                    assertThat(validated.getValue()).isEqualTo(25.0);
                })
                .verifyComplete();
    }
}