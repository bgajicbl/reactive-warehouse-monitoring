package com.warehouse.monitoring.config;

import com.warehouse.monitoring.domain.model.SensorMeasurement;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public SenderOptions<String, SensorMeasurement> senderOptions() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        Map<String, String> producerProps = kafkaProperties.getProducer().getProperties();
        props.putAll(producerProps);

        String acks = kafkaProperties.getProducer().getAcks();
        if (acks != null) {
            props.put(ProducerConfig.ACKS_CONFIG, acks);
        }

        Integer retries = kafkaProperties.getProducer().getRetries();
        if (retries != null) {
            props.put(ProducerConfig.RETRIES_CONFIG, retries);
        }

        return SenderOptions.create(props);
    }

    @Bean
    public KafkaSender<String, SensorMeasurement> kafkaSender(
            SenderOptions<String, SensorMeasurement> senderOptions) {
        return KafkaSender.create(senderOptions);
    }
}
