package com.joiner.ebus.communication.protherm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class AddressUnknownData implements MasterSlaveData {
    
    public static final long KEY = Long.MIN_VALUE;
    
    @Getter
    @Setter
    private byte[] masterData = new byte[0];

    @Getter
    @Setter
    private byte[] slaveData = new byte[0];
    
    @Getter
    @Setter
    private Date date;

    /**
     * 
     * @param data
     */
    public AddressUnknownData(byte[] data) {
        setMasterSlaveData(data);
    }

    @Override
    public long getKey() {
        return KEY;
    }


}
