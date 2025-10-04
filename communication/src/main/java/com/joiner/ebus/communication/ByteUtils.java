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
    
    public static boolean isAllZero(byte[] slave) {
        // kontrola: všechna data jsou nuly?
        boolean allZero = true;
        for (int i = 0; i < slave.length - 1; i++) { // CRC nepočítáme
            if (slave[i] != 0) {
                allZero = false;
                break;
            }
        }
        return allZero;
    }
}
