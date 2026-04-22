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
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.service.converter.Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter;
import com.joiner.ebus.service.event.BurnerControlUnitBlock2MqttEvent;

class BurnerControlUnitBlock2ServiceTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    private Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter converter;

    private ApplicationEventPublisher eventPublisher;

    private BurnerControlUnitBlock2Service service;

    @BeforeEach
    void setUp() {
        converter = mock(Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new BurnerControlUnitBlock2Service();
        ReflectionTestUtils.setField(service, "converter", converter);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    @Test
    void handleFrame_convertsStoresAndPublishesDto() {
        Tg1008B5110102Data data = mock(Tg1008B5110102Data.class);
        BurnerControlUnitBlock2Dto dto = new BurnerControlUnitBlock2Dto().data("block2");
        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new Tg1008B5110102DataReadyEvent(data));

        assertThat(service.getBurnerControlUnitBlock2Dto()).isSameAs(dto);
        verify(converter).convert(data);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(BurnerControlUnitBlock2MqttEvent.class);
        BurnerControlUnitBlock2MqttEvent event = (BurnerControlUnitBlock2MqttEvent) eventCaptor.getValue();
        assertThat(event.getBurnerControlUnitBlock2Dto()).isSameAs(dto);
    }
}