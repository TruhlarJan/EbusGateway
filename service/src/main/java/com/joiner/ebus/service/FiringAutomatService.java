package com.joiner.ebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.DataEventFactory.Tg0315B513DataReadyEvent;
import com.joiner.ebus.model.FiringAutomatDto;
import com.joiner.ebus.service.converter.Tg0315B513DataToFiringAutomatDtoConverter;

import lombok.Getter;

@Service
public class FiringAutomatService {

    @Autowired
    private Tg0315B513DataToFiringAutomatDtoConverter converter;
    
    @Getter
    private FiringAutomatDto firingAutomatDto;
    
    @Async
    @EventListener
    public void handleFrame(Tg0315B513DataReadyEvent event) {
        firingAutomatDto = converter.convert(event.getData());
    }

}
