package com.warehouse.monitoring.config;

import com.warehouse.monitoring.service.KafkaPublisherService;
import com.warehouse.monitoring.service.SensorDataValidator;
import com.warehouse.monitoring.service.SensorMessageParser;
import io.netty.channel.ChannelOption;
import io.netty.channel.socket.DatagramPacket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import reactor.netty.udp.UdpServer;

import java.nio.charset.StandardCharsets;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class UdpListenerConfig {

    private final WarehouseProperties warehouseProperties;
    private final SensorMessageParser messageParser;
    private final SensorDataValidator validator;
    private final KafkaPublisherService publisherService;

    @Value("${warehouse.kafka.topics.temperature:temperature.readings}")
    private String temperatureTopic;

    @Value("${warehouse.kafka.topics.humidity:humidity.readings}")
    private String humidityTopic;

    @EventListener(ApplicationReadyEvent.class)
    public void startUdpListeners() {
        warehouseProperties.getLocations().forEach((locationId, config) -> {
            startTemperatureListener(locationId, config);
            startHumidityListener(locationId, config);
        });
    }

    private void startTemperatureListener(String locationId, WarehouseProperties.LocationConfig config) {
        int port = config.getUdp().getTemperaturePort();

        UdpServer.create()
                .option(ChannelOption.SO_BROADCAST, true)
                .port(port)
                .handle((in, _out) ->
                        in.receiveObject()
                                .cast(DatagramPacket.class)
                                .flatMap(packet -> {
                                    String message = packet.content().toString(StandardCharsets.UTF_8);
                                    log.debug("Received temperature UDP message on port {}: {}", port, message);

                                    return messageParser.parseTemperature(message, config.getId())
                                            .flatMap(measurement -> validator.validate(measurement, config))
                                            .flatMap(measurement -> publisherService.publish(temperatureTopic, measurement))
                                            .doOnSuccess(_v -> log.debug("Successfully published temperature measurement to Kafka topic '{}'", temperatureTopic))
                                            .doOnError(error -> log.error("Error processing temperature message: {}", message, error));
                                })
                                .onErrorContinue((error, _obj) -> log.warn("Temperature listener error (continuing): {}", error.getMessage()))
                )
                .bindNow();

        log.info("Temperature UDP listener started for location '{}' on port {}", locationId, port);
    }

    private void startHumidityListener(String locationId, WarehouseProperties.LocationConfig config) {
        int port = config.getUdp().getHumidityPort();

        UdpServer.create()
                .option(ChannelOption.SO_BROADCAST, true)
                .port(port)
                .handle((in, _out) ->
                        in.receiveObject()
                                .cast(DatagramPacket.class)
                                .flatMap(packet -> {
                                    String message = packet.content().toString(StandardCharsets.UTF_8);
                                    log.debug("Received humidity UDP message on port {}: {}", port, message);

                                    return messageParser.parseHumidity(message, config.getId())
                                            .flatMap(measurement -> validator.validate(measurement, config))
                                            .flatMap(measurement -> publisherService.publish(humidityTopic, measurement))
                                            .doOnSuccess(_v -> log.debug("Successfully published humidity measurement to Kafka topic '{}'", humidityTopic))
                                            .doOnError(error -> log.error("Error processing humidity message: {}", message, error));
                                })
                                .onErrorContinue((error, _obj) -> log.warn("Humidity listener error (continuing): {}", error.getMessage()))
                )
                .bindNow();

        log.info("Humidity UDP listener started for location '{}' on port {}", locationId, port);
    }
}
