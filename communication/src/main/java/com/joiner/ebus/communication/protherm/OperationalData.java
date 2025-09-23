package com.joiner.ebus.communication.protherm;

public interface OperationalData {
    
    /** Source address - Main Control Unit */
    public static int QQ_BYTE = 0x10;

    /** Target address */
    public static int ZZ_BYTE = 0x08;

    /** Vaillant command */
    public static int PB_BYTE = 0xB5;

    /** 00h */
    public static int UNKNOWN_00 = 0x00;

    /** FFh */
    public static int UNKNOWN_FF = 0xFF;

    /** positive acknowledge symbol. */
    public static int ACK = 0x00;

    /** synchronization symbol. */
    public static int SYN = 0xAA;
    
    /**
     * Master data terminated CRC.
     * @return
     */
    byte[] getMasterCrcEndedData();

    /**
     * Data size of the slave response.
     * @return
     */
    int getSlaveSize();

    /**
     * Returns the final bytes that the master should send after reading the response from the slave.
     * Defaults to ACK + SYN.
     */
    default byte[] getMasterFinalData() {
        return new byte[] { ACK , (byte) SYN};
    }

    /**
     * Sets slave data to object after OCR check
     * @param response Unchecked data according to OCR
     */
    void setSlaveData(byte[] response);

    /**
     * Returns checked slave data according to OCR
     * @return Checked data according to OCR
     */
    byte[] getSlaveData();
}
