package com.joiner.ebus.mqtt.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.model.FiringAutomatDto;
import com.joiner.ebus.model.HeaterControllerDto;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.mqtt.service.MqttPublisherService;
import com.joiner.ebus.service.event.BurnerControlUnitBlock0MqttEvent;
import com.joiner.ebus.service.event.BurnerControlUnitBlock1MqttEvent;
import com.joiner.ebus.service.event.BurnerControlUnitBlock2MqttEvent;
import com.joiner.ebus.service.event.FiringAutomatMqttEvent;
import com.joiner.ebus.service.event.HeaterControllerMqttEvent;
import com.joiner.ebus.service.event.RoomControlUnitMqttEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MqttPublisherListener {

    private final MqttPublisherService mqttPublisherService;

    @EventListener
    public void handleRoomControlUnit(RoomControlUnitMqttEvent event) {
        RoomControlUnitDto roomControlUnitDto = event.getRoomControlUnitDto();
        mqttPublisherService.publish("protherm/roomControlUnit", roomControlUnitDto);
    }

    @EventListener
    public void handleBurnerBlock0(BurnerControlUnitBlock0MqttEvent event) {
        BurnerControlUnitBlock0Dto burnerControlUnitBlock0Dto = event.getBurnerControlUnitBlock0Dto();
        mqttPublisherService.publish("protherm/burnerControlUnits/block0", burnerControlUnitBlock0Dto);
    }

    @EventListener
    public void handleBurnerBlock1(BurnerControlUnitBlock1MqttEvent event) {
        BurnerControlUnitBlock1Dto burnerControlUnitBlock1Dto = event.getBurnerControlUnitBlock1Dto();
        mqttPublisherService.publish("protherm/burnerControlUnits/block1", burnerControlUnitBlock1Dto);
    }

    @EventListener
    public void handleBurnerBlock2(BurnerControlUnitBlock2MqttEvent event) {
        BurnerControlUnitBlock2Dto burnerControlUnitBlock2Dto = event.getBurnerControlUnitBlock2Dto();
        mqttPublisherService.publish("protherm/burnerControlUnits/block2", burnerControlUnitBlock2Dto);
    }

    @EventListener
    public void handleHeaterController(HeaterControllerMqttEvent event) {
        HeaterControllerDto heaterControllerDto = event.getHeaterControllerDto();
        mqttPublisherService.publish("protherm/heaterController", heaterControllerDto);
    }

    @EventListener
    public void handleFiringAutomat(FiringAutomatMqttEvent event) {
        FiringAutomatDto firingAutomatDto = event.getFiringAutomatDto();
        mqttPublisherService.publish("protherm/firingAutomat", firingAutomatDto);
    }

}

