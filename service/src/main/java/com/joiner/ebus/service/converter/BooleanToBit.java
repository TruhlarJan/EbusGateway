package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;

public class BooleanToBit implements Converter<Boolean, Integer> {

    @Override
    public Integer convert(Boolean source) {
        if (source == null) {
            return null;
        }
        return source ? 1 : 0;
    }

}
