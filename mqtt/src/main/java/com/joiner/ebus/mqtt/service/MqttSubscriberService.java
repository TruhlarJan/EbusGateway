package com.joiner.ebus.mqtt.service;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.RoomControlUnitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttSubscriberService implements MessageHandler {

    private final RoomControlUnitService roomControlUnitService;

    @Override
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();
        // Loxone optimization
        if (payload.isEmpty()) {
            payload = String.valueOf(0);
        }
        try {
            RoomControlUnitDto roomControlUnitDto = new RoomControlUnitDto();
            if ("protherm/roomControlUnit/leadWaterTargetTemperature".equals(topic)) {
                roomControlUnitDto.setLeadWaterTargetTemperature(Double.valueOf(payload));
            } else if ("protherm/roomControlUnit/serviceWaterTargetTemperature".equals(topic)) {
                roomControlUnitDto.setServiceWaterTargetTemperature(Double.valueOf(payload));
            } else if ("protherm/roomControlUnit/leadWaterHeatingBlocked".equals(topic)) {
                roomControlUnitDto.setLeadWaterHeatingBlocked(Integer.valueOf(payload));
            } else if ("protherm/roomControlUnit/serviceWaterHeatingBlocked".equals(topic)) {
                roomControlUnitDto.setServiceWaterHeatingBlocked(Integer.valueOf(payload));
            } else {
                log.error("Topic {} has not been processed.", topic);
            }
            roomControlUnitService.setRoomControlUnit(roomControlUnitDto);
        } catch (Exception e) {
            log.error("Error parsing MQTT payload: {}", e.getMessage());
        }
    }
}

