package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterSlaveData;

@Component
public class MasterSlaveDataToStringConverter implements Converter<MasterSlaveData, String> {

    @Override
    public String convert(MasterSlaveData source) {
        return String.format("%s %s %s",
                bytesToHex(source.getMasterData()),
                bytesToHex(source.getSlaveData()),
                bytesToHex(source.getMasterFinalData()));
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    
}
