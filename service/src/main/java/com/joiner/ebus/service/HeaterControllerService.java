package com.joiner.ebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.DataEventFactory.Tg0364B512DataReadyEvent;
import com.joiner.ebus.model.HeaterControllerDto;
import com.joiner.ebus.service.converter.Tg0364B512DataToHeaterControllerDtoConverter;
import com.joiner.ebus.service.event.HeaterControllerMqttEvent;

import lombok.Getter;

@Service
public class HeaterControllerService {

    @Autowired
    private Tg0364B512DataToHeaterControllerDtoConverter converter;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Getter
    private HeaterControllerDto heaterControllerDto;

    @Async
    @EventListener
    public void handleFrame(Tg0364B512DataReadyEvent event) {
        heaterControllerDto = converter.convert(event.getData());
        eventPublisher.publishEvent(new HeaterControllerMqttEvent(heaterControllerDto));
    }

}
