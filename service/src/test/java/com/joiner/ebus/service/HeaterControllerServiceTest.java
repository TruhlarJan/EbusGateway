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
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.model.HeaterControllerDto;
import com.joiner.ebus.service.converter.Tg0364B512DataToHeaterControllerDtoConverter;
import com.joiner.ebus.service.event.HeaterControllerMqttEvent;

class HeaterControllerServiceTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    private Tg0364B512DataToHeaterControllerDtoConverter converter;

    private ApplicationEventPublisher eventPublisher;

    private HeaterControllerService service;

    @BeforeEach
    void setUp() {
        converter = mock(Tg0364B512DataToHeaterControllerDtoConverter.class);
        eventPublisher = mock(ApplicationEventPublisher.class);

        service = new HeaterControllerService();
        ReflectionTestUtils.setField(service, "converter", converter);
        ReflectionTestUtils.setField(service, "eventPublisher", eventPublisher);
    }

    @Test
    void handleFrame_convertsStoresAndPublishesDto() {
        Tg0364B512Data data = mock(Tg0364B512Data.class);
        HeaterControllerDto dto = new HeaterControllerDto().data("heater-controller");
        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new Tg0364B512DataReadyEvent(data));

        assertThat(service.getHeaterControllerDto()).isSameAs(dto);
        verify(converter).convert(data);

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue()).isInstanceOf(HeaterControllerMqttEvent.class);
        HeaterControllerMqttEvent event = (HeaterControllerMqttEvent) eventCaptor.getValue();
        assertThat(event.getHeaterControllerDto()).isSameAs(dto);
    }
}