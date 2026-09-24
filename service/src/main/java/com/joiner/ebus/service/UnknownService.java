package com.joiner.ebus.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

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
    private final Map<String, UnknownDto> unknowns = new LinkedHashMap<>();

    @Async
    @EventListener
    public void handleFrame(TgUnknownDataReadyEvent event) {

        UnknownDto dto = converter.convert(event.getData());
        String data = dto.getData();

        if (data == null) {
            return;
        }

        UnknownDto existing = unknowns.get(data);

        if (existing == null) {
            if (unknowns.size() >= CAPACITY) {
                String firstKey = unknowns.keySet().iterator().next();
                unknowns.remove(firstKey);
            }

            dto.setDateTimes(new ArrayList<>());
            dto.getDateTimes().add(OffsetDateTime.now());

            unknowns.put(data, dto);
        } else {
            existing.getDateTimes().add(OffsetDateTime.now());
        }
    }
}