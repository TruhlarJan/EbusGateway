package com.joiner.ebus.communication.protherm;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Tg0364B512Data implements MasterData {

    public static final long KEY = 3731069469186L;

    public static final int YY_INDEX = 6;

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
    public Tg0364B512Data(byte[] data) {
        setMasterData(data);
        setDate(new Date());
    }

    @Override
    public long getKey() {
        return KEY;
    }

}
