package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;

public class BitToBoolean implements Converter<Integer, Boolean> {

    @Override
    public Boolean convert(Integer source) {
        if (source == null) {
            return null;
        }
        return source != 0;
    }

}
