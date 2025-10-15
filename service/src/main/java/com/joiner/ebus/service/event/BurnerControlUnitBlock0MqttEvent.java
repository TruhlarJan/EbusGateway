package com.joiner.ebus.service.event;

import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BurnerControlUnitBlock0MqttEvent {

    @Getter
    private final BurnerControlUnitBlock0Dto payload;

}

