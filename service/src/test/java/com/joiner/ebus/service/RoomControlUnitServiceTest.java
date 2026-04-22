package com.joiner.ebus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.DataCollector;
import com.joiner.ebus.communication.link.DataEventFactory;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.converter.RoomControlUnitDtoToTg1008B510DataConverter;
import com.joiner.ebus.service.converter.Tg1008B510DataToRoomControlUnitDtoConverter;
import com.joiner.ebus.service.event.RoomControlUnitMqttEvent;

class RoomControlUnitServiceTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    private Tg1008B510DataToRoomControlUnitDtoConverter inboundConverter;

    private RoomControlUnitDtoToTg1008B510DataConverter outboundConverter;

    private DataCollector dataCollector;

    private ApplicationEventPublisher eventPublisher;

    private RoomControlUnitService service;

    @BeforeEach
    void setUp() {
        inboundConverter = mock(Tg1008B510DataToRoomControlUnitDtoConverter.class);
        outboundConverter = mock(RoomControlUnitDtoToTg1008B510DataConverter.class);
        dataCollector = mock(DataCollector.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new RoomControlUnitService();
        ReflectionTestUtils.setField(service, "converter", inboundConverter);
        ReflectionTestUtils.setField(service, "converter2", outboundConverter);
        ReflectionTestUtils.setField(service, "dataCollector", dataCollector);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    @Test
    void handleFrame_whenFrameIsNewer_mergesStateAndPublishesEvent() {
        service.getRoomControlUnitDto().setServiceWaterTargetTemperature(52.0);

        Tg1008B510Data data = mock(Tg1008B510Data.class);
        when(data.getDate()).thenReturn(new Date(1_000L));

        RoomControlUnitDto converted = new RoomControlUnitDto()
                .data("room-control")
                .leadWaterTargetTemperature(48.5)
                .serviceWaterHeatingBlocked(1);
        when(inboundConverter.convert(data)).thenReturn(converted);

        service.handleFrame(dataEventFactory.new Tg1008B510DataReadyEvent(data));

        RoomControlUnitDto state = service.getRoomControlUnitDto();
        assertThat(state.getData()).isEqualTo("room-control");
        assertThat(state.getLeadWaterTargetTemperature()).isEqualTo(48.5);
        assertThat(state.getServiceWaterTargetTemperature()).isEqualTo(52.0);
        assertThat(state.getServiceWaterHeatingBlocked()).isEqualTo(1);
        verify(inboundConverter).convert(data);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(RoomControlUnitMqttEvent.class);
        RoomControlUnitMqttEvent event = (RoomControlUnitMqttEvent) eventCaptor.getValue();
        assertThat(event.getRoomControlUnitDto()).isSameAs(state);
    }

    @Test
    void handleFrame_whenFrameIsOlderThanLastSet_ignoresIt() {
        AtomicLong lastSetTime = (AtomicLong) ReflectionTestUtils.getField(service, "lastSetTime");
        assertThat(lastSetTime).isNotNull();
        lastSetTime.set(2_000L);

        Tg1008B510Data data = mock(Tg1008B510Data.class);
        when(data.getDate()).thenReturn(new Date(1_000L));

        service.handleFrame(dataEventFactory.new Tg1008B510DataReadyEvent(data));

        verifyNoInteractions(inboundConverter);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void setRoomControlUnit_updatesStateAndSendsConvertedTelegram() {
        service.getRoomControlUnitDto().setServiceWaterTargetTemperature(52.0);

        RoomControlUnitDto update = new RoomControlUnitDto()
                .data("updated-room-control")
                .leadWaterTargetTemperature(48.5)
                .leadWaterHeatingBlocked(1);

        Tg1008B510Data outboundData = mock(Tg1008B510Data.class);
        when(outboundConverter.convert(any(RoomControlUnitDto.class))).thenReturn(outboundData);

        service.setRoomControlUnit(update);

        RoomControlUnitDto state = service.getRoomControlUnitDto();
        assertThat(update.getDateTime()).isNotNull();
        assertThat(state.getData()).isEqualTo("updated-room-control");
        assertThat(state.getLeadWaterTargetTemperature()).isEqualTo(48.5);
        assertThat(state.getLeadWaterHeatingBlocked()).isEqualTo(1);
        assertThat(state.getServiceWaterTargetTemperature()).isEqualTo(52.0);
        assertThat(state.getDateTime()).isEqualTo(update.getDateTime());

        ArgumentCaptor<RoomControlUnitDto> dtoCaptor = ArgumentCaptor.forClass(RoomControlUnitDto.class);
        verify(outboundConverter).convert(dtoCaptor.capture());
        assertThat(dtoCaptor.getValue()).isSameAs(state);
        verify(dataCollector).sendDataImmidiately(outboundData);
    }
}