package com.joiner.ebus.mqtt.service;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.RoomControlUnitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MqttSubscriberService implements MessageHandler {

    private final ObjectMapper objectMapper;
    private final RoomControlUnitService roomControlUnitService;

    @Override
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();
        try {
            if ("protherm/roomControlUnit/update".equals(topic)) {
                RoomControlUnitDto dto = objectMapper.readValue(payload, RoomControlUnitDto.class);
                roomControlUnitService.setRoomControlUnit(dto);
                System.out.println("✅ RoomControlUnit updated via MQTT");
            }
        } catch (Exception e) {
            log.error("❌ Chyba při parsování MQTT payloadu: {}", e.getMessage());
        }
    }
}

