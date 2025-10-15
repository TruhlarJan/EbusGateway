package com.joiner.ebus.service;

import java.util.ArrayDeque;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.DataEventFactory.TgUnknownDataReadyEvent;
import com.joiner.ebus.model.UnknownDto;
import com.joiner.ebus.service.converter.TgUnknownDataToUnknownDtoConverter;

import lombok.Getter;

@Service
public class UnknownService {

    private static final int CAPACITY = 100;

    @Autowired
    private TgUnknownDataToUnknownDtoConverter converter;
    
    @Getter
    ArrayDeque<UnknownDto> unknowns = new ArrayDeque<>(CAPACITY);

    @Async
    @EventListener
    public void handleFrame(TgUnknownDataReadyEvent event) {
        if (unknowns.size() == CAPACITY) {
            unknowns.removeFirst();
        }
        unknowns.addLast(converter.convert(event.getData()));
    }

}
