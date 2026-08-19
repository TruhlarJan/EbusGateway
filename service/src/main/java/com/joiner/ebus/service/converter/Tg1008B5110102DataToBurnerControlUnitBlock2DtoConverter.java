package com.joiner.ebus.service.converter;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;

@Component
public class Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter implements Converter<Tg1008B5110102Data, BurnerControlUnitBlock2Dto> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public BurnerControlUnitBlock2Dto convert(Tg1008B5110102Data source) {
        byte[] masterData = source.getMasterData();
        byte[] slaveData = source.getSlaveData();
        String data = String.format("%s  %s", conversionService.convert(masterData, String.class), conversionService.convert(slaveData, String.class));

        byte vv = slaveData[Tg1008B5110102Data.VV_INDEX];

        BurnerControlUnitBlock2Dto burnerControlUnitBlock2Dto = new BurnerControlUnitBlock2Dto();
        burnerControlUnitBlock2Dto.setData(data);
        burnerControlUnitBlock2Dto.setDateTime(OffsetDateTime.now());
        burnerControlUnitBlock2Dto.setHeatingEnabled(vv & 1);
        burnerControlUnitBlock2Dto.setServiceWaterEnabled((vv >> 1) & 1);
        return burnerControlUnitBlock2Dto;
    }

}
