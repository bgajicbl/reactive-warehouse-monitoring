package com.monitoring.central.service;

import com.warehouse.monitoring.domain.model.SensorMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.kafka.receiver.KafkaReceiver;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageListener {

  private final KafkaReceiver<String, SensorMeasurement> temperatureReceiver;
  private final KafkaReceiver<String, SensorMeasurement> humidityReceiver;
  private final ThresholdMonitor thresholdMonitor;
  private final AlarmService alarmService;

  @EventListener(ApplicationReadyEvent.class)
  public void startListening() {
    log.info("Starting Kafka message listeners...");

    // Listen to temperature measurements
    temperatureReceiver
        .receive()
        .doOnNext(
            record -> {
              log.info("Received temperature message: {}", record.value());
              record.receiverOffset().acknowledge();
            })
        .flatMap(record -> thresholdMonitor.checkThreshold(record.value()))
        .flatMap(alarmService::processAlarm)
        .doOnError(error -> log.error("Error processing temperature message", error))
        .onErrorContinue(
            (error, ignored) ->
                log.error("Temperature listener encountered an error, continuing...", error))
        .subscribe();

    // Listen to humidity measurements
    humidityReceiver
        .receive()
        .doOnNext(
            record -> {
              log.info("Received humidity message: {}", record.value());
              record.receiverOffset().acknowledge();
            })
        .flatMap(record -> thresholdMonitor.checkThreshold(record.value()))
        .flatMap(alarmService::processAlarm)
        .doOnError(error -> log.error("Error processing humidity message", error))
        .onErrorContinue(
            (error, ignored) ->
                log.error("Humidity listener encountered an error, continuing...", error))
        .subscribe();

    log.info("Kafka message listeners started successfully");
  }
}
