package com.joiner.ebus.service.converter;

import static com.joiner.ebus.communication.protherm.Address03h15hB5h13hData.YY_INDEX;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.model.FiringAutomatDto;

@Component
public class Address03h15hB5h13hDataToFiringAutomatDtoConverter implements Converter<Address03h15hB5h13hData, FiringAutomatDto> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public FiringAutomatDto convert(Address03h15hB5h13hData source) {
        byte[] masterData = source.getMasterData();

        FiringAutomatDto firingAutomatDto = new FiringAutomatDto();
        firingAutomatDto.setData(conversionService.convert(masterData, String.class));
        firingAutomatDto.setLocalDate(LocalDate.now());

        byte b = masterData[YY_INDEX];
        if (b == 0x00) {
            firingAutomatDto.setInternalPump(0);
        } else if (b == 0x64) {
            firingAutomatDto.setInternalPump(1);
        } else {
            firingAutomatDto.setInternalPump(-1);
        }
        return firingAutomatDto;
    }

}
