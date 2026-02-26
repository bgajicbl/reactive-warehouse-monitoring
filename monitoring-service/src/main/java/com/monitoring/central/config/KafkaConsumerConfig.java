package com.monitoring.central.config;

import com.warehouse.monitoring.domain.model.SensorMeasurement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

  private final KafkaProperties kafkaProperties;

  @Value("${monitoring.kafka.topics.temperature:temperature.readings}")
  private String temperatureTopic;

  @Value("${monitoring.kafka.topics.humidity:humidity.readings}")
  private String humidityTopic;

  @Bean
  public ReceiverOptions<String, SensorMeasurement> receiverOptions() {
    Map<String, Object> props = new HashMap<>();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());

    String groupId = kafkaProperties.getConsumer().getGroupId();
    if (groupId != null) {
      props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    }

    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JacksonJsonDeserializer.class);

    String autoOffsetReset = kafkaProperties.getConsumer().getAutoOffsetReset();
    if (autoOffsetReset != null) {
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
    }

    Boolean enableAutoCommit = kafkaProperties.getConsumer().getEnableAutoCommit();
    if (enableAutoCommit != null) {
      props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
    }

    Map<String, String> consumerProps = kafkaProperties.getConsumer().getProperties();
    props.putAll(consumerProps);

    props.put(JacksonJsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    props.put(JacksonJsonDeserializer.VALUE_DEFAULT_TYPE, SensorMeasurement.class.getName());

    return ReceiverOptions.create(props);
  }

  // ...existing code...

  @Bean
  public KafkaReceiver<String, SensorMeasurement> temperatureReceiver(
      ReceiverOptions<String, SensorMeasurement> receiverOptions) {
    return KafkaReceiver.create(
        receiverOptions.subscription(Collections.singleton(temperatureTopic)));
  }

  @Bean
  public KafkaReceiver<String, SensorMeasurement> humidityReceiver(
      ReceiverOptions<String, SensorMeasurement> receiverOptions) {
    return KafkaReceiver.create(receiverOptions.subscription(Collections.singleton(humidityTopic)));
  }
}
