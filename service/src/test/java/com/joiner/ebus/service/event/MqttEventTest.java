package com.joiner.ebus.service.event;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.model.FiringAutomatDto;
import com.joiner.ebus.model.HeaterControllerDto;
import com.joiner.ebus.model.RoomControlUnitDto;

class MqttEventTest {

    @Test
    void burnerControlUnitBlock0MqttEvent_exposesProvidedDto() {
        BurnerControlUnitBlock0Dto dto = new BurnerControlUnitBlock0Dto();

        BurnerControlUnitBlock0MqttEvent event = new BurnerControlUnitBlock0MqttEvent(dto);

        assertThat(event.getBurnerControlUnitBlock0Dto()).isSameAs(dto);
    }

    @Test
    void burnerControlUnitBlock1MqttEvent_exposesProvidedDto() {
        BurnerControlUnitBlock1Dto dto = new BurnerControlUnitBlock1Dto();

        BurnerControlUnitBlock1MqttEvent event = new BurnerControlUnitBlock1MqttEvent(dto);

        assertThat(event.getBurnerControlUnitBlock1Dto()).isSameAs(dto);
    }

    @Test
    void burnerControlUnitBlock2MqttEvent_exposesProvidedDto() {
        BurnerControlUnitBlock2Dto dto = new BurnerControlUnitBlock2Dto();

        BurnerControlUnitBlock2MqttEvent event = new BurnerControlUnitBlock2MqttEvent(dto);

        assertThat(event.getBurnerControlUnitBlock2Dto()).isSameAs(dto);
    }

    @Test
    void firingAutomatMqttEvent_exposesProvidedDto() {
        FiringAutomatDto dto = new FiringAutomatDto();

        FiringAutomatMqttEvent event = new FiringAutomatMqttEvent(dto);

        assertThat(event.getFiringAutomatDto()).isSameAs(dto);
    }

    @Test
    void heaterControllerMqttEvent_exposesProvidedDto() {
        HeaterControllerDto dto = new HeaterControllerDto();

        HeaterControllerMqttEvent event = new HeaterControllerMqttEvent(dto);

        assertThat(event.getHeaterControllerDto()).isSameAs(dto);
    }

    @Test
    void roomControlUnitMqttEvent_exposesProvidedDto() {
        RoomControlUnitDto dto = new RoomControlUnitDto();

        RoomControlUnitMqttEvent event = new RoomControlUnitMqttEvent(dto);

        assertThat(event.getRoomControlUnitDto()).isSameAs(dto);
    }
}