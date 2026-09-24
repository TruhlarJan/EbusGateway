package com.joiner.ebus.service.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.TgUnknownData;
import com.joiner.ebus.model.UnknownDto;

@Component
public class TgUnknownDataToUnknownDtoConverter implements Converter<TgUnknownData, UnknownDto> {

    @Autowired
    private ConversionService conversionService;

    @Override
    public UnknownDto convert(TgUnknownData source) {
        byte[] masterData = source.getMasterData();

        UnknownDto unknownDto = new UnknownDto();
        unknownDto.setData(conversionService.convert(masterData, String.class));
        return unknownDto;
    }

}
