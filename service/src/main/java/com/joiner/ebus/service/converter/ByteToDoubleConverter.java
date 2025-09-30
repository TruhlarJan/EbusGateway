package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;

public class ByteToDoubleConverter implements Converter<Integer, Double> {

    @Override
    public Double convert(Integer source) {
        if (source == null) return null;
        if (source < 0 || source > 255) {
            throw new IllegalArgumentException("Value out of byte range: " + source);
        }
        return source / 2.0;
    }

}
