package com.joiner.ebus.communication.protherm;

import lombok.Getter;

@Getter
public class AddressUnknownData implements SlaveData {
    
    public static final long KEY = Long.MIN_VALUE;
    
    private byte[] address;
    private byte[] data;

    public AddressUnknownData(byte[] address, byte[] data) {
        this.address = address;
        this.data = data;
    }

    @Override
    public long getKey() {
        return KEY;
    }

}
