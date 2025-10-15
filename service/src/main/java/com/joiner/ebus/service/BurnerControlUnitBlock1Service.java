package com.joiner.ebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110101DataReadyEvent;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.service.converter.Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter;

import lombok.Getter;

@Service
public class BurnerControlUnitBlock1Service {

    @Autowired
    private Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter converter;
    
    @Getter
    private BurnerControlUnitBlock1Dto burnerControlUnitBlock1Dto;
    
    @Async
    @EventListener
    public void handleFrame(Tg1008B5110101DataReadyEvent event) {
        burnerControlUnitBlock1Dto = converter.convert(event.getData());
    }

}
