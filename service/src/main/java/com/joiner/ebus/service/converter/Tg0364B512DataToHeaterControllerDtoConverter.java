package com.joiner.ebus.service.converter;

import static com.joiner.ebus.communication.protherm.Tg0364B512Data.YY_INDEX;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.model.HeaterControllerDto;

@Component
public class Tg0364B512DataToHeaterControllerDtoConverter implements Converter<Tg0364B512Data, HeaterControllerDto> {

    @Autowired
    private ConversionService conversionService;
    
    @Override
    public HeaterControllerDto convert(Tg0364B512Data source) {
        byte[] masterData = source.getMasterData();

        HeaterControllerDto heaterControllerDto = new HeaterControllerDto();
        heaterControllerDto.setData(conversionService.convert(masterData, String.class));
        heaterControllerDto.setDateTime(OffsetDateTime.now());

        byte b = masterData[YY_INDEX];
        if (b == 0x00) {
            heaterControllerDto.setWaterCirculatingPump(1);
        } else if (b == 0x64) {
            heaterControllerDto.setWaterCirculatingPump(2);
        } else if (b == (byte) 0xFE) {
            heaterControllerDto.setWaterCirculatingPump(0);
        } else {
            heaterControllerDto.setWaterCirculatingPump(-1);
        }
        return heaterControllerDto;
    }

}
