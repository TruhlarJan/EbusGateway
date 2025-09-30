package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ByteToDoubleConverter implements Converter<Byte, Double> {

    @Override
    public Double convert(Byte source) {
        if (source == null) return null;
        if (source < 0 || source > 255) {
            throw new IllegalArgumentException("Value out of byte range: " + source);
        }
        return source / 2.0;
    }

}
