package com.monitoring.central.service;

import com.warehouse.monitoring.domain.model.Alarm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HumidityAlarmHandler {

  public void handle(Alarm alarm) {
    log.info(
        "Handling humidity alarm for sensor {} in warehouse {}",
        alarm.getSensorId(),
        alarm.getWarehouseId());
  }
}
