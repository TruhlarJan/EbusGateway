package com.joiner.ebus.service;

import static com.joiner.ebus.service.converter.source.Booleans.of;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.DataCollector;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.service.dto.RoomControlUnitDto;

@Service
public class RoomControlService {

    private DataCollector dataCollector;
    
    public RoomControlService(DataCollector dataCollector) {
        this.dataCollector = dataCollector;
    }

    @Autowired
    private ConversionService conversionService;

    public void setRoomControlUnit(RoomControlUnitDto roomControlUnitDto) {
        int m8 =  conversionService.convert(roomControlUnitDto.getLeadWaterTargetTemperature(), Byte.class);
        int m9 =  conversionService.convert(roomControlUnitDto.getServiceWaterTargetTemperature(), Byte.class);
        int m12 = conversionService.convert(of(roomControlUnitDto.isLeadWaterHeatingBlocked(), roomControlUnitDto.isServiceWaterHeatingBlocked()), Byte.class);
        dataCollector.sendDataImmidiately(new Tg1008B510Data(m8, m9, m12));
    }

}
