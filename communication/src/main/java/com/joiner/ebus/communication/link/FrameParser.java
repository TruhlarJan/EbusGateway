package com.joiner.ebus.communication.link;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;

import lombok.Getter;

@Component
public class FrameParser {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Getter
    private Map<Long, byte[]> map = new HashMap<>();
    
    public void save(byte[] byteArray) {
        byte[] address = Arrays.copyOfRange(byteArray, 0, 5);
        byte[] data = Arrays.copyOfRange(byteArray, 5, byteArray.length);
        
        long key = bytesToLong(address);
        if (key == Address03h64hB5h12hData.KYE) {
            publisher.publishEvent(new FrameParsedEvent(this, key, new Address03h64hB5h12hData(address, data)));
        } else if (key == Address03h15hB5h13hData.KYE) {
            publisher.publishEvent(new FrameParsedEvent(this, key, new Address03h15hB5h13hData(address, data)));
        } else {
            map.put(key, byteArray);
        }
    }

    // uděláme z prvních 5 bajtů jedno číslo typu long
    public long bytesToLong(byte[] byteArray) {
        long key = 0;
        for (int i = 0; i < 5; i++) {
            key = (key << 8) | (byteArray[i] & 0xFFL);
        }
        return key;
    }

    // Rekonstrukce 5 bajtů z long klíče
    public byte[] longToBytes(long key) {
        byte[] result = new byte[5];
        for (int i = 4; i >= 0; i--) {
            result[i] = (byte) (key & 0xFF);
            key >>= 8;
        }
        return result;
    }

}
