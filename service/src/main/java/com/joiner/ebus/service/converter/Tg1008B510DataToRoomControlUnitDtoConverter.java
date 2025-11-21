package com.joiner.ebus.service.converter;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.model.RoomControlUnitDto;

@Component
public class Tg1008B510DataToRoomControlUnitDtoConverter implements Converter<Tg1008B510Data, RoomControlUnitDto> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public RoomControlUnitDto convert(Tg1008B510Data source) {
        byte[] masterData = source.getMasterData();
        byte m8 = masterData[Tg1008B510Data.M8_INDEX];
        byte m9 = masterData[Tg1008B510Data.M9_INDEX];
        byte m12 = masterData[Tg1008B510Data.M12_INDEX];

        RoomControlUnitDto roomControlUnitDto = new RoomControlUnitDto();
        roomControlUnitDto.setData(conversionService.convert(masterData, String.class));
        roomControlUnitDto.setDateTime(OffsetDateTime.now());
        roomControlUnitDto.setLeadWaterTargetTemperature((m8 & 0xFF) / 2.0);
        roomControlUnitDto.setServiceWaterTargetTemperature((m9 & 0xFF) / 2.0);
        roomControlUnitDto.setLeadWaterHeatingBlocked(m12 & 1);
        roomControlUnitDto.setServiceWaterHeatingBlocked((m12 >> 2) & 1);
        return roomControlUnitDto;
    }

}
