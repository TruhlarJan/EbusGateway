package com.joiner.ebus.communication.protherm;

import lombok.Getter;

@Getter
public class O3h64hB5h12hOperationData implements MasterData {

    public static final long KYE = 14574490114L;

    private byte[] address;
    private byte[] data;

    public O3h64hB5h12hOperationData(byte[] address, byte[] data) {
        this.address = address;
        this.data = data;
    }

}
