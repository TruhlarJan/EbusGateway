package com.joiner.ebus.mqtt.listener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

class MqttPublisherListenerTest {

    private MqttPublisherService mqttPublisherService;

    private MqttPublisherListener listener;

    @BeforeEach
    void setUp() {
        mqttPublisherService = mock(MqttPublisherService.class);
        listener = new MqttPublisherListener(mqttPublisherService);
    }

    @Test
    void handleRoomControlUnit_publishesDtoToExpectedTopic() {
        RoomControlUnitDto dto = new RoomControlUnitDto().data("room-control");

        listener.handleRoomControlUnit(new RoomControlUnitMqttEvent(dto));

        verify(mqttPublisherService).publish("protherm/roomControlUnit", dto);
    }

    @Test
    void handleBurnerBlock0_publishesDtoToExpectedTopic() {
        BurnerControlUnitBlock0Dto dto = new BurnerControlUnitBlock0Dto().data("block0");

        listener.handleBurnerBlock0(new BurnerControlUnitBlock0MqttEvent(dto));

        verify(mqttPublisherService).publish("protherm/burnerControlUnits/block0", dto);
    }

    @Test
    void handleBurnerBlock1_publishesDtoToExpectedTopic() {
        BurnerControlUnitBlock1Dto dto = new BurnerControlUnitBlock1Dto().data("block1");

        listener.handleBurnerBlock1(new BurnerControlUnitBlock1MqttEvent(dto));

        verify(mqttPublisherService).publish("protherm/burnerControlUnits/block1", dto);
    }

    @Test
    void handleBurnerBlock2_publishesDtoToExpectedTopic() {
        BurnerControlUnitBlock2Dto dto = new BurnerControlUnitBlock2Dto().data("block2");

        listener.handleBurnerBlock2(new BurnerControlUnitBlock2MqttEvent(dto));

        verify(mqttPublisherService).publish("protherm/burnerControlUnits/block2", dto);
    }

    @Test
    void handleHeaterController_publishesDtoToExpectedTopic() {
        HeaterControllerDto dto = new HeaterControllerDto().data("heater-controller");

        listener.handleHeaterController(new HeaterControllerMqttEvent(dto));

        verify(mqttPublisherService).publish("protherm/heaterController", dto);
    }

    @Test
    void handleFiringAutomat_publishesDtoToExpectedTopic() {
        FiringAutomatDto dto = new FiringAutomatDto().data("firing-automat");

        listener.handleFiringAutomat(new FiringAutomatMqttEvent(dto));

        verify(mqttPublisherService).publish("protherm/firingAutomat", dto);
    }
}