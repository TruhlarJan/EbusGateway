package com.joiner.ebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110102DataReadyEvent;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.service.converter.Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter;

import lombok.Getter;

@Service
public class BurnerControlUnitBlock2Service {

    @Autowired
    private Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter converter;
    
    @Getter
    private BurnerControlUnitBlock2Dto burnerControlUnitBlock2Dto;
    
    @Async
    @EventListener
    public void handleFrame(Tg1008B5110102DataReadyEvent event) {
        burnerControlUnitBlock2Dto = converter.convert(event.getData());
    }

}
