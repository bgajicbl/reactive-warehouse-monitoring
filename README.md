# Reactive Warehouse Monitoring System

A reactive system for monitoring temperature and humidity sensors across multiple warehouses with automatic threshold-based alarm activation.

## Architecture Overview

This project implements a **Reactive Event-Driven Architecture** using **Spring Boot 4.0.3** with **Java 25** featuring the following components:

### System Components

1. **Warehouse Service** - Spring Boot reactive UDP listener and event publisher
    - Listens for temperature measurements on UDP ports 3344 and 3346
    - Listens for humidity measurements on UDP ports 3355 and 3357
    - Parses sensor data format: `sensor_id=X; value=Y`
    - Publishes validated measurements to message broker

2. **Central Monitoring Service** - Spring Boot event consumer and alarm system
    - Subscribes to sensor measurement events
    - Monitors configured thresholds:
        - Temperature: 35°C
        - Humidity: 50%
    - Raises alarms (visible in logs/console) when thresholds are exceeded

3. **Message Broker** - Apache Kafka
    - Decouples warehouse service from monitoring service
    - Provides durability and scalability
    - Event streaming backbone

### Technology Stack

- **Framework**: Spring Boot 4.0.3
- **Language**: Java 25
- **Reactive Framework**: Spring WebFlux
- **Message Broker**: Spring for Apache Kafka
- **Build Tool**: Maven 3.x
- **Testing**: Spring Boot Test, JUnit 5, Reactor Test
- **Networking**: Reactor Netty (included with Spring WebFlux)
- **Serialization**: Jackson JSON (via Spring Kafka)

## Quick Start

### Prerequisites
- JDK 25+ (required for Spring Boot 4.0.3)
- Maven 3.6+
- Docker & Docker Compose (for Kafka infrastructure)

### 1. Start Message Broker (Kafka)

```bash
docker-compose up -d
```

### 2. Start Central Monitoring Service

```bash
cd monitoring-service
mvn spring-boot:run
```

### 3. Start Warehouse Service

```bash
cd warehouse-service
mvn spring-boot:run
```

### 4. Send Sensor Measurements

#### Using netcat (recommended for testing):

**Temperature sensor (below threshold)**:
```bash
echo "sensor_id=t1; value=30" | nc -u localhost 3344
```

**Temperature sensor (exceeds threshold)**:
```bash
echo "sensor_id=t1; value=36" | nc -u localhost 3344
```

**Humidity sensor (below threshold)**:
```bash
echo "sensor_id=h1; value=40" | nc -u localhost 3355
```

**Humidity sensor (exceeds threshold)**:
```bash
echo "sensor_id=h1; value=55" | nc -u localhost 3355
```

### 5. Observe Alarms

Monitor the Central Monitoring Service console output:

```
================================================================================
[ALARM] TEMPERATURE threshold exceeded! | Sensor: t1 | Warehouse: warehouse-1 | Value: 36.00°C | Threshold: 35.00°C | Time: 2026-02-25T10:30:00Z
================================================================================
[ALARM] HUMIDITY threshold exceeded! | Sensor: h1 | Warehouse: warehouse-1 | Value: 55.00% | Threshold: 50.00% | Time: 2026-02-25T10:32:00Z
================================================================================
```


