package com.joiner.ebus.mqtt.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.joiner.ebus.mqtt.service.MqttPublisherService;
import com.joiner.ebus.service.event.BurnerControlUnitBlock0MqttEvent;
import com.joiner.ebus.service.event.BurnerControlUnitBlock1MqttEvent;
import com.joiner.ebus.service.event.BurnerControlUnitBlock2MqttEvent;

@Component
public class MqttPublisherListener {

    private final MqttPublisherService mqttPublisherService;

    public MqttPublisherListener(MqttPublisherService mqttPublisherService) {
        this.mqttPublisherService = mqttPublisherService;
    }

    // ====== Burner Control Unit Block0 ======
    @EventListener
    public void handleBurnerBlock0(BurnerControlUnitBlock0MqttEvent event) {
        mqttPublisherService.publish("protherm/burnerControlUnit/block0", event.getBurnerControlUnitBlock0Dto());
    }

    // ====== Burner Control Unit Block1 ======
    @EventListener
    public void handleBurnerBlock1(BurnerControlUnitBlock1MqttEvent event) {
        mqttPublisherService.publish("protherm/burnerControlUnit/block1", event.getBurnerControlUnitBlock1Dto());
    }

    // ====== Burner Control Unit Block2 ======
    @EventListener
    public void handleBurnerBlock2(BurnerControlUnitBlock2MqttEvent event) {
        mqttPublisherService.publish("protherm/burnerControlUnit/block2", event.getBurnerControlUnitBlock2Dto());
    }
}

