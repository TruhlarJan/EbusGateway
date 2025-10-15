package com.joiner.ebus.mqtt.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.joiner.ebus.mqtt.service.MqttPublisherService;
import com.joiner.ebus.service.event.BurnerControlUnitBlock0MqttEvent;

@Component
public class MqttPublisherListener {

    private final MqttPublisherService mqttPublisherService;

    public MqttPublisherListener(MqttPublisherService mqttPublisherService) {
        this.mqttPublisherService = mqttPublisherService;
    }

    @EventListener
    public void handleBurnerBlock0(BurnerControlUnitBlock0MqttEvent event) {
        mqttPublisherService.publish("protherm/burnerControlUnit/block0", event.getPayload());
    }
}

