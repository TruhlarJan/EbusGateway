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
import com.joiner.ebus.communication.protherm.Tg0315B513Data;
import com.joiner.ebus.model.FiringAutomatDto;
import com.joiner.ebus.service.converter.Tg0315B513DataToFiringAutomatDtoConverter;
import com.joiner.ebus.service.event.FiringAutomatMqttEvent;

class FiringAutomatServiceTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    private Tg0315B513DataToFiringAutomatDtoConverter converter;

    private ApplicationEventPublisher eventPublisher;

    private FiringAutomatService service;

    @BeforeEach
    void setUp() {
        converter = mock(Tg0315B513DataToFiringAutomatDtoConverter.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new FiringAutomatService();
        ReflectionTestUtils.setField(service, "converter", converter);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    @Test
    void handleFrame_convertsStoresAndPublishesDto() {
        Tg0315B513Data data = mock(Tg0315B513Data.class);
        FiringAutomatDto dto = new FiringAutomatDto().data("firing-automat");
        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new Tg0315B513DataReadyEvent(data));

        assertThat(service.getFiringAutomatDto()).isSameAs(dto);
        verify(converter).convert(data);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(FiringAutomatMqttEvent.class);
        FiringAutomatMqttEvent event = (FiringAutomatMqttEvent) eventCaptor.getValue();
        assertThat(event.getFiringAutomatDto()).isSameAs(dto);
    }
}