package com.joiner.ebus.service.converter;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;

@Component
public class Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter implements Converter<Tg1008B5110100Data, BurnerControlUnitBlock0Dto> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public BurnerControlUnitBlock0Dto convert(Tg1008B5110100Data source) {
        byte[] masterSlaveData = source.getMasterSlaveData();

        BurnerControlUnitBlock0Dto burnerControlUnitBlock0Dto = new BurnerControlUnitBlock0Dto();
        burnerControlUnitBlock0Dto.setData(conversionService.convert(masterSlaveData, String.class));
        burnerControlUnitBlock0Dto.setDateTime(OffsetDateTime.now());
        return burnerControlUnitBlock0Dto;
    }

}
