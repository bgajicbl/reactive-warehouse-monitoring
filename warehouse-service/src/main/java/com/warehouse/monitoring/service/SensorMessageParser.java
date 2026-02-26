package com.warehouse.monitoring.service;

import com.warehouse.monitoring.domain.model.HumidityMeasurement;
import com.warehouse.monitoring.domain.model.TemperatureMeasurement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SensorMessageParser {

    // Pattern to match: sensor_id=X; value=Y
    private static final Pattern SENSOR_PATTERN = Pattern.compile(
            "sensor_id=([^;]+);\\s*value=([0-9.\\-]+)", Pattern.CASE_INSENSITIVE
    );

    public Mono<TemperatureMeasurement> parseTemperature(String message, String warehouseId) {
        return parse(message)
                .map(parsed -> {
                    TemperatureMeasurement measurement = new TemperatureMeasurement();
                    measurement.setSensorId(parsed.sensorId());
                    measurement.setValue(parsed.value());
                    measurement.setTimestamp(Instant.now());
                    measurement.setWarehouseId(warehouseId);
                    log.debug("Parsed temperature: sensorId={}, value={}", parsed.sensorId(), parsed.value());
                    return measurement;
                })
                .onErrorResume(e -> {
                    log.error("Failed to parse temperature message: {}", message, e);
                    if (e instanceof IllegalArgumentException && e.getMessage() != null
                            && e.getMessage().contains("Message cannot be null or empty")) {
                        return Mono.error(e);
                    }
                    return Mono.error(new IllegalArgumentException("Invalid temperature message format: " + message, e));
                });
    }

    public Mono<HumidityMeasurement> parseHumidity(String message, String warehouseId) {
        return parse(message)
                .map(parsed -> {
                    HumidityMeasurement measurement = new HumidityMeasurement();
                    measurement.setSensorId(parsed.sensorId());
                    measurement.setValue(parsed.value());
                    measurement.setTimestamp(Instant.now());
                    measurement.setWarehouseId(warehouseId);
                    log.debug("Parsed humidity: sensorId={}, value={}", parsed.sensorId(), parsed.value());
                    return measurement;
                })
                .onErrorResume(e -> {
                    log.error("Failed to parse humidity message: {}", message, e);
                    if (e instanceof IllegalArgumentException && e.getMessage() != null
                            && e.getMessage().contains("Message cannot be null or empty")) {
                        return Mono.error(e);
                    }
                    return Mono.error(new IllegalArgumentException("Invalid humidity message format: " + message, e));
                });
    }

    private Mono<ParsedData> parse(String message) {
        return Mono.fromCallable(() -> {
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Message cannot be null or empty");
            }

            Matcher matcher = SENSOR_PATTERN.matcher(message.trim());
            if (!matcher.find()) {
                throw new IllegalArgumentException("Message does not match expected format: sensor_id=X; value=Y");
            }

            String sensorId = matcher.group(1).trim();
            double value = Double.parseDouble(matcher.group(2).trim());

            return new ParsedData(sensorId, value);
        });
    }

    private record ParsedData(String sensorId, double value) {}
}
