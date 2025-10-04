package com.joiner.ebus.communication.link;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h10hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.AddressUnknownData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

@Component
public class FrameParser {
    
    @Autowired
    private ByteUtils utils;
    
    public MasterSlaveData getMasterSlaveData(byte[] data) {
        byte[] address = Arrays.copyOfRange(data, 0, 6);
        
        long key = utils.getKey(address);
        if (key == Address10h08hB5h10hData.KEY) {
            return new Address10h08hB5h10hData(data);
        } else if (key == Address10h08hB5h11h01h00hData.KEY) {
            return new Address10h08hB5h11h01h00hData(data);
        } else if (key == Address10h08hB5h11h01h01hData.KEY) {
            return new Address10h08hB5h11h01h01hData(data);
        } else if (key == Address10h08hB5h11h01h02hData.KEY) {
            return new Address10h08hB5h11h01h02hData(data);
        } else if (key == Address03h64hB5h12hData.KEY) {
            return new Address03h64hB5h12hData(data);
         } else if (key == Address03h15hB5h13hData.KEY) {
             return new Address03h15hB5h13hData(data);
        } else {
            return new AddressUnknownData(data);
        }
    }

}
