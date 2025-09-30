package com.joiner.ebus.communication.protherm;

import lombok.Getter;

@Getter
public class Address03h64hB5h12hData implements SlaveData {

    public static final long KEY = 3731069469186L;

    private byte[] address;
    private byte[] data;

    public Address03h64hB5h12hData(byte[] address, byte[] data) {
        this.address = address;
        this.data = data;
    }

}
