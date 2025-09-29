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
    
    public long save(byte[] byteArray) {
        byte[] address = Arrays.copyOfRange(byteArray, 0, 6);
        byte[] data = Arrays.copyOfRange(byteArray, 6, byteArray.length);
        
        long key = getKey(address);
        if (key == Address03h64hB5h12hData.KEY) {
            publisher.publishEvent(new FrameParsedEvent(this, key, new Address03h64hB5h12hData(address, data)));
        } else if (key == Address03h15hB5h13hData.KEY) {
            publisher.publishEvent(new FrameParsedEvent(this, key, new Address03h15hB5h13hData(address, data)));
        } else {
            map.put(key, byteArray);
        }
        return key;
    }

    // uděláme z prvních 6 bajtů jedno číslo typu long
    public long getKey(byte[] byteArray) {
        long key = 0;
        for (int i = 0; i < 6; i++) {
            key = (key << 8) | (byteArray[i] & 0xFFL);
        }
        return key;
    }

}
