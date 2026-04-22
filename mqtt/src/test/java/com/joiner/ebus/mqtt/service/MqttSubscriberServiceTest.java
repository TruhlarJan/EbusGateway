package com.joiner.ebus.mqtt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.RoomControlUnitService;

class MqttSubscriberServiceTest {

    private RoomControlUnitService roomControlUnitService;

    private MqttSubscriberService subscriberService;

    @BeforeEach
    void setUp() {
        roomControlUnitService = mock(RoomControlUnitService.class);
        subscriberService = new MqttSubscriberService(roomControlUnitService);
    }

    @Test
    void handleMessage_mapsLeadWaterTargetTemperature() {
        subscriberService.handleMessage(message("protherm/roomControlUnit/leadWaterTargetTemperature", "48.5"));

        RoomControlUnitDto dto = capturePublishedDto();
        assertThat(dto.getLeadWaterTargetTemperature()).isEqualTo(48.5);
        assertThat(dto.getServiceWaterTargetTemperature()).isNull();
        assertThat(dto.getLeadWaterHeatingBlocked()).isNull();
        assertThat(dto.getServiceWaterHeatingBlocked()).isNull();
    }

    @Test
    void handleMessage_mapsServiceWaterTargetTemperature() {
        subscriberService.handleMessage(message("protherm/roomControlUnit/serviceWaterTargetTemperature", "52.0"));

        RoomControlUnitDto dto = capturePublishedDto();
        assertThat(dto.getServiceWaterTargetTemperature()).isEqualTo(52.0);
        assertThat(dto.getLeadWaterTargetTemperature()).isNull();
    }

    @Test
    void handleMessage_mapsLeadWaterHeatingBlocked() {
        subscriberService.handleMessage(message("protherm/roomControlUnit/leadWaterHeatingBlocked", "1"));

        RoomControlUnitDto dto = capturePublishedDto();
        assertThat(dto.getLeadWaterHeatingBlocked()).isEqualTo(1);
        assertThat(dto.getServiceWaterHeatingBlocked()).isNull();
    }

    @Test
    void handleMessage_mapsEmptyPayloadToZeroBeforeParsing() {
        subscriberService.handleMessage(message("protherm/roomControlUnit/serviceWaterHeatingBlocked", ""));

        RoomControlUnitDto dto = capturePublishedDto();
        assertThat(dto.getServiceWaterHeatingBlocked()).isZero();
    }

    @Test
    void handleMessage_forUnknownTopicDelegatesEmptyDto() {
        subscriberService.handleMessage(message("protherm/roomControlUnit/unknownProperty", "42"));

        RoomControlUnitDto dto = capturePublishedDto();
        assertThat(dto.getLeadWaterTargetTemperature()).isNull();
        assertThat(dto.getServiceWaterTargetTemperature()).isNull();
        assertThat(dto.getLeadWaterHeatingBlocked()).isNull();
        assertThat(dto.getServiceWaterHeatingBlocked()).isNull();
    }

    @Test
    void handleMessage_whenPayloadCannotBeParsed_doesNotCallService() {
        subscriberService.handleMessage(message("protherm/roomControlUnit/leadWaterTargetTemperature", "not-a-number"));

        verifyNoInteractions(roomControlUnitService);
    }

    private RoomControlUnitDto capturePublishedDto() {
        ArgumentCaptor<RoomControlUnitDto> captor = ArgumentCaptor.forClass(RoomControlUnitDto.class);
        verify(roomControlUnitService).setRoomControlUnit(captor.capture());
        return captor.getValue();
    }

    private static Message<String> message(String topic, String payload) {
        return MessageBuilder.withPayload(payload)
                .setHeader(MqttHeaders.RECEIVED_TOPIC, topic)
                .build();
    }
}