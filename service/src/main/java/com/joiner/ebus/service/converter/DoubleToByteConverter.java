package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class DoubleToByteConverter implements Converter<Double, Byte> {

    @Override
    public Byte convert(Double source) {
        if (source == null) return null;
        // protože hodnoty budou násobky 0.5, scaled bude celé číslo
        Byte intVal = (byte) Math.round(source * 2);

        if (intVal < 0 || intVal > 255) {
            throw new IllegalArgumentException("Value out of byte range: " + intVal);
        }
        return intVal;
    }
}
