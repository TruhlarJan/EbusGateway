package com.joiner.ebus.service.event;

import com.joiner.ebus.model.FiringAutomatDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FiringAutomatMqttEvent {

    @Getter
    private final FiringAutomatDto firingAutomatDto;

}
