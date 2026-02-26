package com.monitoring.central.service;

import com.warehouse.monitoring.domain.model.Alarm;
import com.warehouse.monitoring.domain.model.SensorType;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;

  private final TemperatureAlarmHandler temperatureHandler;
  private final HumidityAlarmHandler humidityHandler;

  public Mono<Void> processAlarm(Alarm alarm) {
    return Mono.fromRunnable(
        () -> {
          logAlarm(alarm);

          if (alarm.getSensorType() == SensorType.TEMPERATURE) {
            temperatureHandler.handle(alarm);
          } else if (alarm.getSensorType() == SensorType.HUMIDITY) {
            humidityHandler.handle(alarm);
          }
        });
  }

  private void logAlarm(Alarm alarm) {
    String message = formatAlarmMessage(alarm);
    String separator = "=".repeat(80);

    // ANSI color codes
    String red = "\u001B[31m";
    String reset = "\u001B[0m";

    log.warn("{}{}{}", red, separator, reset);
    log.warn("{}{}{}", red, message, reset);
    log.warn("{}{}{}", red, separator, reset);
  }

  private String formatAlarmMessage(Alarm alarm) {
    String unit = alarm.getSensorType() == SensorType.TEMPERATURE ? "°C" : "%";

    return String.format(
        "[ALARM] %s threshold exceeded! | Sensor: %s | Warehouse: %s | Value: %.2f%s | Threshold: %.2f%s | Time: %s",
        alarm.getSensorType(),
        alarm.getSensorId(),
        alarm.getWarehouseId(),
        alarm.getValue(),
        unit,
        alarm.getThreshold(),
        unit,
        FORMATTER.format(alarm.getTimestamp()));
  }
}
