package com.joiner.ebus.communication.protherm;

import lombok.Getter;

@Getter
public class Address03h15hB5h13hData implements MasterData {

    public static final long KYE = 13249090307L;
    
    private byte[] address;
    private byte[] data;

    public Address03h15hB5h13hData(byte[] address, byte[] data) {
        this.address = address;
        this.data = data;
    }

}
