package com.joiner.ebus.service.event;

import com.joiner.ebus.model.RoomControlUnitDto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoomControlUnitMqttEvent {

    @Getter
    private final RoomControlUnitDto roomControlUnitDto;

}
