package com.joiner.ebus.mqtt.service;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MqttPublisherService {

    private final MessageChannel mqttOutboundChannel;
    private final ObjectMapper objectMapper;

    public MqttPublisherService(MessageChannel mqttOutboundChannel, ObjectMapper objectMapper) {
        this.mqttOutboundChannel = mqttOutboundChannel;
        this.objectMapper = objectMapper;
    }

    public void publish(String topic, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(payload);
            mqttOutboundChannel.send(MessageBuilder.withPayload(json)
                    .setHeader(MqttHeaders.TOPIC, topic)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

