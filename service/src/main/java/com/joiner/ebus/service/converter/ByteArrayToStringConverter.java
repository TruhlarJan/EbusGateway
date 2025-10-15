package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ByteArrayToStringConverter implements Converter<byte[], String> {

    @Override
    public String convert(byte[] source) {
        StringBuilder sb = new StringBuilder();
        for (byte b : source) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
    
}
