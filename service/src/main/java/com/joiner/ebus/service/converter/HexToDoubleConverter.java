package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.service.converter.source.Hex;

@Component
public class HexToDoubleConverter implements Converter<Hex, Double> {

    @Override
    public Double convert(Hex source) {
        Byte value = source.value();
        if (value == null) {
            return null;
        }
        if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Value out of byte range: " + value);
        }
        return value / 2.0;
    }

}
