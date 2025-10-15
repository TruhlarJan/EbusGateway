package com.joiner.ebus.service.event;

import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BurnerControlUnitBlock1MqttEvent {

    @Getter
    private final BurnerControlUnitBlock1Dto burnerControlUnitBlock1Dto;

}

