package com.joiner.ebus.communication.protherm;

import lombok.Getter;

@Getter
public class O3h15hB5h13hOperationData implements MasterData {

    public static final long KYE = 13249090307L;
    
    private byte[] address;
    private byte[] data;

    public O3h15hB5h13hOperationData(byte[] address, byte[] data) {
        this.address = address;
        this.data = data;
    }

}
