package com.joiner.ebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.DataEventFactory.Address03h15hB5h13hDataReadyEvent;
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
    public void handleFrame(Address03h15hB5h13hDataReadyEvent event) {
        firingAutomatDto = converter.convert(event.getData());
    }

}
