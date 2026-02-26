package com.warehouse.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "warehouse")
@Getter
@Setter
public class WarehouseProperties {

    private Map<String, LocationConfig> locations = new HashMap<>();

    @Getter
    @Setter
    public static class LocationConfig {

        private String id;
        private UdpProperties udp = new UdpProperties();
        private ValidationProperties validation = new ValidationProperties();

        @Getter
        @Setter
        public static class UdpProperties {
            private int temperaturePort = 3344;
            private int humidityPort = 3355;
            private int bufferSize = 1024;
        }

        @Getter
        @Setter
        public static class ValidationProperties {
            private SensorConstraints temperature = new SensorConstraints(-50.0, 100.0);
            private SensorConstraints humidity = new SensorConstraints(0.0, 100.0);

            @Getter
            @Setter
            public static class SensorConstraints {
                private double min;
                private double max;

                public SensorConstraints(double min, double max) {
                    this.min = min;
                    this.max = max;
                }
            }
        }
    }
}
