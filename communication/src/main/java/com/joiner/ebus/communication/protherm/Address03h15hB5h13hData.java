package com.joiner.ebus.communication.protherm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Address03h15hB5h13hData implements MasterData {

    public static final long KEY = 3391767118598L;
    
    @Getter
    @Setter
    private byte[] masterData = new byte[0];
    
    @Getter
    @Setter
    private Date date;

    /**
     * 
     */
    public Address03h15hB5h13hData(byte[] data) {
        setMasterData(data);
    }

    @Override
    public long getKey() {
        return KEY;
    }

}
