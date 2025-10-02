package com.joiner.ebus.communication.link;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.AddressUnknownData;
import com.joiner.ebus.communication.protherm.SlaveData;

@Component
public class FrameParser {
    
    @Autowired
    private ByteUtils utils;
    
    public SlaveData getSlaveData(byte[] byteArray) {
        byte[] address = Arrays.copyOfRange(byteArray, 0, 6);
        byte[] data = Arrays.copyOfRange(byteArray, 6, byteArray.length);
        
        long key = utils.getKey(address);
        if (key == Address03h64hB5h12hData.KEY) {
           return new Address03h64hB5h12hData(address, data);
        } else if (key == Address03h15hB5h13hData.KEY) {
            return new Address03h15hB5h13hData(address, data);
        } else {
            return new AddressUnknownData(address, data);
        }
    }

}
