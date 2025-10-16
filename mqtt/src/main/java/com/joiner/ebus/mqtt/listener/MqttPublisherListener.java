package com.joiner.ebus.mqtt.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.joiner.ebus.mqtt.service.MqttPublisherService;
import com.joiner.ebus.service.event.BurnerControlUnitBlock0MqttEvent;
import com.joiner.ebus.service.event.BurnerControlUnitBlock1MqttEvent;
import com.joiner.ebus.service.event.BurnerControlUnitBlock2MqttEvent;
import com.joiner.ebus.service.event.FiringAutomatMqttEvent;
import com.joiner.ebus.service.event.HeaterControllerMqttEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MqttPublisherListener {

    private final MqttPublisherService mqttPublisherService;

    @EventListener
    public void handleBurnerBlock0(BurnerControlUnitBlock0MqttEvent event) {
        mqttPublisherService.publish("protherm/burnerControlUnit/block0", event.getBurnerControlUnitBlock0Dto());
    }

    @EventListener
    public void handleBurnerBlock1(BurnerControlUnitBlock1MqttEvent event) {
        mqttPublisherService.publish("protherm/burnerControlUnit/block1", event.getBurnerControlUnitBlock1Dto());
    }

    @EventListener
    public void handleBurnerBlock2(BurnerControlUnitBlock2MqttEvent event) {
        mqttPublisherService.publish("protherm/burnerControlUnit/block2", event.getBurnerControlUnitBlock2Dto());
    }

    @EventListener
    public void handleHeaterController(HeaterControllerMqttEvent event) {
        mqttPublisherService.publish("protherm/heaterController", event.getHeaterControllerDto());
    }

    @EventListener
    public void handleFiringAutomat(FiringAutomatMqttEvent event) {
        mqttPublisherService.publish("protherm/firingAutomat", event.getFiringAutomatDto());
    }

}

