package com.joiner.ebus.communication.protherm;

import java.util.Date;

public interface MasterData {

    /** positive acknowledge symbol. */
    public static int ACK = 0x00;

    /** synchronization symbol. */
    public static int SYN = 0xAA;

    /**
     * Address key of the MasterSlave object.
     * 
     * @return
     */
    long getKey();
    
    /**
     * Master data terminated CRC.
     * 
     * @return
     */
    byte[] getMasterData();

    /**
     * 
     * @param data
     */
    void setMasterData(byte[] masterData);

    /**
     * Date of accepted slave data.
     * 
     * @return
     */
    Date getDate();

    /**
     * Date of responded correct master slave data
     * 
     * @param date
     */
    void setDate(Date date);

}
