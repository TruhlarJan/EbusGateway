package com.joiner.ebus.communication.protherm;

import com.joiner.ebus.communication.MasterData;

import lombok.Getter;

@Getter
public class Address03h64hB5h12hData implements MasterData {

    public static final long KYE = 14574490114L;

    private byte[] address;
    private byte[] data;

    public Address03h64hB5h12hData(byte[] address, byte[] data) {
        this.address = address;
        this.data = data;
    }

}
