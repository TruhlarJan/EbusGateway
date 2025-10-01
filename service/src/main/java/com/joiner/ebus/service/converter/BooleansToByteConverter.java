package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.service.converter.source.Booleans;

@Component
public class BooleansToByteConverter implements Converter<Booleans, Byte> {

    @Override
    public Byte convert(Booleans source) {
        boolean b0 = source.isB0();
        boolean b2 = source.isB2();
        
        int value = 0;
        if (b0) {
            value += 1;
        }
        if (b2) {
            value += 1 << 2;
        }
        return (byte) (value & 0xFF); 
    }

}
