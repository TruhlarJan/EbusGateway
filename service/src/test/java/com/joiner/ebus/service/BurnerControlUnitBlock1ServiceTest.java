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
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.service.converter.Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter;
import com.joiner.ebus.service.event.BurnerControlUnitBlock1MqttEvent;

class BurnerControlUnitBlock1ServiceTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    private Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter converter;

    private ApplicationEventPublisher eventPublisher;

    private BurnerControlUnitBlock1Service service;

    @BeforeEach
    void setUp() {
        converter = mock(Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new BurnerControlUnitBlock1Service();
        ReflectionTestUtils.setField(service, "converter", converter);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    @Test
    void handleFrame_convertsStoresAndPublishesDto() {
        Tg1008B5110101Data data = mock(Tg1008B5110101Data.class);
        BurnerControlUnitBlock1Dto dto = new BurnerControlUnitBlock1Dto().data("block1");
        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new Tg1008B5110101DataReadyEvent(data));

        assertThat(service.getBurnerControlUnitBlock1Dto()).isSameAs(dto);
        verify(converter).convert(data);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(BurnerControlUnitBlock1MqttEvent.class);
        BurnerControlUnitBlock1MqttEvent event = (BurnerControlUnitBlock1MqttEvent) eventCaptor.getValue();
        assertThat(event.getBurnerControlUnitBlock1Dto()).isSameAs(dto);
    }
}