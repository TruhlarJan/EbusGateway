package com.joiner.ebus.communication.protherm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Tg0315B513Data implements MasterData {

    public static final long KEY = 3391767118598L;
    
    public static final int YY_INDEX = 6;
    
    @Getter
    @Setter
    private byte[] masterData = new byte[0];
    
    @Getter
    @Setter
    private Date date;

    /**
     * 
     */
    public Tg0315B513Data(byte[] data) {
        setMasterData(data);
        setDate(new Date());
    }

    @Override
    public long getKey() {
        return KEY;
    }

}
