package com.joiner.ebus.service.event;

import com.joiner.ebus.model.HeaterControllerDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HeaterControllerMqttEvent {

    @Getter
    private final HeaterControllerDto heaterControllerDto;

}
