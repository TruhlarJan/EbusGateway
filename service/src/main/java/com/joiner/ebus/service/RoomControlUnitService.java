package com.joiner.ebus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.DataCollector;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.converter.RoomControlUnitDtoToTg1008B510DataConverter;

import lombok.Getter;

@Service
public class RoomControlUnitService {

    @Autowired
    private RoomControlUnitDtoToTg1008B510DataConverter converter;

    @Autowired
    private DataCollector dataCollector;

    @Getter
    private RoomControlUnitDto roomControlUnitDto;
    
    public void setRoomControlUnit(RoomControlUnitDto roomControlUnitDto) {
        this.roomControlUnitDto = roomControlUnitDto;
        dataCollector.sendDataImmidiately(converter.convert(roomControlUnitDto));
    }

}
