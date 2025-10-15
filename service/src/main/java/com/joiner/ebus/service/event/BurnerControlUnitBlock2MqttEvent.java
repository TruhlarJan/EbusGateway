package com.joiner.ebus.service.event;

import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BurnerControlUnitBlock2MqttEvent {

    @Getter
    private final BurnerControlUnitBlock2Dto burnerControlUnitBlock2Dto;

}

