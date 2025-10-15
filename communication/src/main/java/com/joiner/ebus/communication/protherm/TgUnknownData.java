package com.joiner.ebus.communication.protherm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TgUnknownData implements MasterData {
    
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
    public TgUnknownData(byte[] data) {
        setMasterData(data);
        setDate(new Date());
    }

    @Override
    public long getKey() {
        return KEY;
    }


}
