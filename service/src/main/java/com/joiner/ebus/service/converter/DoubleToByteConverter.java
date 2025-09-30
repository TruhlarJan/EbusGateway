package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;

public class DoubleToByteConverter implements Converter<Double, Integer> {

    @Override
    public Integer convert(Double source) {
        if (source == null) return null;
        double scaled = source * 2;

        // protože hodnoty budou násobky 0.5, scaled bude celé číslo
        int intVal = (int) Math.round(scaled);

        if (intVal < 0 || intVal > 255) {
            throw new IllegalArgumentException("Value out of byte range: " + intVal);
        }
        return intVal;
    }
}
