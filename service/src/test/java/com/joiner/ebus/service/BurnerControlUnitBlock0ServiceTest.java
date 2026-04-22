package com.joiner.ebus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.link.DataEventFactory;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.service.converter.Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter;
import com.joiner.ebus.service.event.BurnerControlUnitBlock0MqttEvent;

class BurnerControlUnitBlock0ServiceTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    private Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter converter;

    private ApplicationEventPublisher eventPublisher;

    private BurnerControlUnitBlock0Service service;

    @BeforeEach
    void setUp() {
        converter = mock(Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new BurnerControlUnitBlock0Service();
        ReflectionTestUtils.setField(service, "converter", converter);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    @Test
    void handleFrame_convertsStoresAndPublishesDto() {
        Tg1008B5110100Data data = mock(Tg1008B5110100Data.class);
        BurnerControlUnitBlock0Dto dto = new BurnerControlUnitBlock0Dto().data("block0");
        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new Tg1008B5110100DataReadyEvent(data));

        assertThat(service.getBurnerControlUnitBlock0Dto()).isSameAs(dto);
        verify(converter).convert(data);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(BurnerControlUnitBlock0MqttEvent.class);
        BurnerControlUnitBlock0MqttEvent event = (BurnerControlUnitBlock0MqttEvent) eventCaptor.getValue();
        assertThat(event.getBurnerControlUnitBlock0Dto()).isSameAs(dto);
    }
}