package com.joiner.ebus.service.converter;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;

@Component
public class Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter implements Converter<Tg1008B5110101Data, BurnerControlUnitBlock1Dto> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public BurnerControlUnitBlock1Dto convert(Tg1008B5110101Data source) {
        byte[] masterSlaveData = source.getMasterSlaveData();
        byte[] slaveData = source.getSlaveData();
        byte vt = slaveData[Tg1008B5110101Data.VT_INDEX];
        byte nt = slaveData[Tg1008B5110101Data.NT_INDEX];
        byte st = slaveData[Tg1008B5110101Data.ST_INDEX];
        byte vv = slaveData[Tg1008B5110101Data.VV_INDEX];
        
        BurnerControlUnitBlock1Dto burnerControlUnitBlock1Dto = new BurnerControlUnitBlock1Dto();
        burnerControlUnitBlock1Dto.setData(conversionService.convert(masterSlaveData, String.class));
        burnerControlUnitBlock1Dto.setLocalDate(LocalDate.now());
        burnerControlUnitBlock1Dto.setLeadWaterTemperature((vt & 0xFF) / 2.0);
        burnerControlUnitBlock1Dto.setReturnWaterTemperature((nt & 0xFF) / 2.0);
        burnerControlUnitBlock1Dto.setServiceWaterTemperature((st & 0xFF) / 2.0);
        burnerControlUnitBlock1Dto.setHeating(vv & 1);
        burnerControlUnitBlock1Dto.setServiceWater((vv >> 1) & 1);

        return burnerControlUnitBlock1Dto;
    }

}
