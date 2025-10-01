package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class BitToBoolean implements Converter<Bit, Boolean> {

    @Override
    public Boolean convert(Bit source) {
        Integer value = source.value();
        if (value == null) {
            return null;
        }
        return value != 0;
    }

}
