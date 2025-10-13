package com.joiner.ebus.communication.protherm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AddressUnknownData implements MasterData {
    
    public static final long KEY = Long.MIN_VALUE;
    
    @Getter
    @Setter
    private byte[] masterData = new byte[0];

    @Getter
    @Setter
    private Date date;

    /**
     * 
     * @param data
     */
    public AddressUnknownData(byte[] data) {
        setMasterData(data);
    }

    @Override
    public long getKey() {
        return KEY;
    }


}
