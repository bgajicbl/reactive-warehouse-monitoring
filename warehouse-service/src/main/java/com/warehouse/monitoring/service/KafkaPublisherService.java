package com.warehouse.monitoring.service;

import com.warehouse.monitoring.domain.model.SensorMeasurement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisherService {

    private final KafkaSender<String, SensorMeasurement> kafkaSender;

    public Mono<Void> publish(String topic, SensorMeasurement measurement) {
        ProducerRecord<String, SensorMeasurement> record = new ProducerRecord<>(
                topic,
                measurement.getSensorId(),
                measurement
        );

        return kafkaSender.send(Mono.just(SenderRecord.create(record, null)))
                .doOnNext(result -> log.debug("Published to topic '{}': sensorId={}, type={}, value={}",
                        topic,
                        measurement.getSensorId(),
                        measurement.getSensorType(),
                        measurement.getValue()))
                .doOnError(error -> log.error("Failed to publish to topic '{}': sensorId={}, type={}",
                        topic,
                        measurement.getSensorId(),
                        measurement.getSensorType(),
                        error))
                .then();
    }

}
