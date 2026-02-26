package com.warehouse.monitoring.service;

import com.warehouse.monitoring.config.WarehouseProperties;
import com.warehouse.monitoring.domain.model.SensorMeasurement;
import com.warehouse.monitoring.domain.model.SensorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorDataValidator {

    public <T extends SensorMeasurement> Mono<T> validate(T measurement, WarehouseProperties.LocationConfig config) {
        return Mono.fromCallable(() -> {
            // Check sensor ID
            if (measurement.getSensorId() == null || measurement.getSensorId().trim().isEmpty()) {
                throw new IllegalArgumentException("Sensor ID cannot be null or empty");
            }

            // Check warehouse ID
            if (measurement.getWarehouseId() == null || measurement.getWarehouseId().trim().isEmpty()) {
                throw new IllegalArgumentException("Warehouse ID cannot be null or empty");
            }

            // Check timestamp
            if (measurement.getTimestamp() == null) {
                throw new IllegalArgumentException("Timestamp cannot be null");
            }

            // Validate based on sensor type
            SensorType type = measurement.getSensorType();
            validateValue(measurement.getValue(), type, config);

            log.debug("Validated measurement: sensorId={}, type={}, value={}, warehouse={}",
                    measurement.getSensorId(), type, measurement.getValue(), measurement.getWarehouseId());

            return measurement;
        });
    }

    private void validateValue(double value, SensorType type, WarehouseProperties.LocationConfig config) {
        if (type == SensorType.TEMPERATURE) {
            double min = config.getValidation().getTemperature().getMin();
            double max = config.getValidation().getTemperature().getMax();

            if (value < min || value > max) {
                throw new IllegalArgumentException(
                        String.format("Temperature value %.2f is out of valid range [%.2f, %.2f]",
                                value, min, max));
            }
        } else if (type == SensorType.HUMIDITY) {
            double min = config.getValidation().getHumidity().getMin();
            double max = config.getValidation().getHumidity().getMax();

            if (value < min || value > max) {
                throw new IllegalArgumentException(
                        String.format("Humidity value %.2f is out of valid range [%.2f, %.2f]",
                                value, min, max));
            }
        }
    }
}
