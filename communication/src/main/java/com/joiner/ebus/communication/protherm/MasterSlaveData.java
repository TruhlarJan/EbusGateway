package com.joiner.ebus.communication.protherm;

public interface MasterSlaveData {
    
    /** Source address - Main Control Unit */
    public static int QQ = 0x10;

    /** Target address */
    public static int ZZ = 0x08;

    /** Vaillant command */
    public static int PB = 0xB5;

    /** positive acknowledge symbol. */
    public static int ACK = 0x00;

    /** synchronization symbol. */
    public static int SYN = 0xAA;
    
    /**
     * Master data terminated CRC.
     * @return
     */
    byte[] getMasterStartData();

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
