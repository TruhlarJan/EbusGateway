package com.joiner.ebus.communication.protherm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Address03h64hB5h12hData implements MasterSlaveData {

    public static final long KEY = 3731069469186L;

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
    public Address03h64hB5h12hData(byte[] data) {
        setMasterSlaveData(data);
    }

    @Override
    public long getKey() {
        return KEY;
    }

}
