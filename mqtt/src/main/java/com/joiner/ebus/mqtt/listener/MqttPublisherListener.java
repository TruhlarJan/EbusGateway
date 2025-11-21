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
        mqttPublisherService.publish("protherm/roomControlUnitDto", roomControlUnitDto);
    }

    @EventListener
    public void handleBurnerBlock0(BurnerControlUnitBlock0MqttEvent event) {
        BurnerControlUnitBlock0Dto burnerControlUnitBlock0Dto = event.getBurnerControlUnitBlock0Dto();
        mqttPublisherService.publish("protherm/burnerControlUnit/block0/data", burnerControlUnitBlock0Dto.getData());
        mqttPublisherService.publish("protherm/burnerControlUnit/block0/dateTime", burnerControlUnitBlock0Dto.getDateTime());
        mqttPublisherService.publish("protherm/burnerControlUnit/block0/flueGasTemperature", burnerControlUnitBlock0Dto.getFlueGasTemperature());
        mqttPublisherService.publish("protherm/burnerControlUnit/block0/waterPressure", burnerControlUnitBlock0Dto.getWaterPressure());
        mqttPublisherService.publish("protherm/burnerControlUnit/block0/flameBurningPower", burnerControlUnitBlock0Dto.getFlameBurningPower());
    }

    @EventListener
    public void handleBurnerBlock1(BurnerControlUnitBlock1MqttEvent event) {
        BurnerControlUnitBlock1Dto burnerControlUnitBlock1Dto = event.getBurnerControlUnitBlock1Dto();
        mqttPublisherService.publish("protherm/burnerControlUnit/block1/data", burnerControlUnitBlock1Dto.getData());
        mqttPublisherService.publish("protherm/burnerControlUnit/block1/dateTime", burnerControlUnitBlock1Dto.getDateTime());
        mqttPublisherService.publish("protherm/burnerControlUnit/block1/heating", burnerControlUnitBlock1Dto.getHeating());
        mqttPublisherService.publish("protherm/burnerControlUnit/block1/leadWaterTemperature", burnerControlUnitBlock1Dto.getLeadWaterTemperature());
        mqttPublisherService.publish("protherm/burnerControlUnit/block1/returnWaterTemperature", burnerControlUnitBlock1Dto.getReturnWaterTemperature());
        mqttPublisherService.publish("protherm/burnerControlUnit/block1/serviceWater", burnerControlUnitBlock1Dto.getServiceWater());
        mqttPublisherService.publish("protherm/burnerControlUnit/block1/serviceWaterTemperature", burnerControlUnitBlock1Dto.getServiceWaterTemperature());
    }

    @EventListener
    public void handleBurnerBlock2(BurnerControlUnitBlock2MqttEvent event) {
        BurnerControlUnitBlock2Dto burnerControlUnitBlock2Dto = event.getBurnerControlUnitBlock2Dto();
        mqttPublisherService.publish("protherm/burnerControlUnit/block2/data", burnerControlUnitBlock2Dto.getData());
        mqttPublisherService.publish("protherm/burnerControlUnit/block2/dateTime", burnerControlUnitBlock2Dto.getDateTime());
        mqttPublisherService.publish("protherm/burnerControlUnit/block2/heating", burnerControlUnitBlock2Dto.getHeating());
        mqttPublisherService.publish("protherm/burnerControlUnit/block2/serviceWater", burnerControlUnitBlock2Dto.getServiceWater());
    }

    @EventListener
    public void handleHeaterController(HeaterControllerMqttEvent event) {
        HeaterControllerDto heaterControllerDto = event.getHeaterControllerDto();
        mqttPublisherService.publish("protherm/heaterController/data", heaterControllerDto.getData());
        mqttPublisherService.publish("protherm/heaterController/dateTime", heaterControllerDto.getDateTime());
        mqttPublisherService.publish("protherm/heaterController/hotWaterCirculatingPump", heaterControllerDto.getHotWaterCirculatingPump());
    }

    @EventListener
    public void handleFiringAutomat(FiringAutomatMqttEvent event) {
        FiringAutomatDto firingAutomatDto = event.getFiringAutomatDto();
        mqttPublisherService.publish("protherm/firingAutomat/data", firingAutomatDto.getData());
        mqttPublisherService.publish("protherm/firingAutomat/dateTime", firingAutomatDto.getDateTime());
        mqttPublisherService.publish("protherm/firingAutomat/internalPump", firingAutomatDto.getInternalPump());
    }

}

