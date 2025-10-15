package com.joiner.ebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110100DataReadyEvent;
import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.service.converter.Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter;
import com.joiner.ebus.service.event.BurnerControlUnitBlock0MqttEvent;

import lombok.Getter;

@Service
public class BurnerControlUnitBlock0Service {

    @Autowired
    private Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter converter;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Getter
    private BurnerControlUnitBlock0Dto burnerControlUnitBlock0Dto;
    
    @Async
    @EventListener
    public void handleFrame(Tg1008B5110100DataReadyEvent event) {
        burnerControlUnitBlock0Dto = converter.convert(event.getData());
        eventPublisher.publishEvent(new BurnerControlUnitBlock0MqttEvent(this, burnerControlUnitBlock0Dto));
    }

}
