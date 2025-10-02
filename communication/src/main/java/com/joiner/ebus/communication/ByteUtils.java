package com.joiner.ebus.communication;

import org.springframework.stereotype.Component;

@Component
public class ByteUtils {

    // uděláme z prvních 6 bajtů jedno číslo typu long
    public long getKey(byte[] byteArray) {
        long key = 0;
        for (int i = 0; i < 6; i++) {
            key = (key << 8) | (byteArray[i] & 0xFFL);
        }
        return key;
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
    
}
