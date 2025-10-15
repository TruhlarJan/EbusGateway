package com.joiner.ebus.communication.protherm;

import java.util.Arrays;

import com.joiner.ebus.communication.EbusCrc;

public interface MasterSlaveData extends MasterData {

    /** Source address - Main Control Unit */
    public static int QQ = 0x10;

    /** Target address */
    public static int ZZ = 0x08;

    /** Vaillant command */
    public static int PB = 0xB5;


    /**
     * Returns checked slave data according to OCR
     * 
     * @return Checked data according to OCR
     */
    byte[] getSlaveData();

    /**
     * 
     * @param slaveData
     */
    void setSlaveData(byte[] slaveData);

    /**
     * 
     * @param masterSlaveData
     */
    void setMasterSlaveData(byte[] masterSlaveData);
    
    /**
     * 
     * @return
     */
    byte[] getMasterSlaveData();

    /**
     * 
     * @param slaveData
     */
    default void parseSlaveData(byte[] masterSlaveData) {
        if (getMasterSlaveData().length != masterSlaveData.length) {
            return;
        }
        byte[] slave = Arrays.copyOfRange(masterSlaveData, getMasterData().length, masterSlaveData.length);
        int crcResponsed = slave[slave.length - 1] & 0xFF;
        int crcComputed = EbusCrc.computeCrc(Arrays.copyOf(slave, slave.length - 1)) & 0xFF;
        if (crcResponsed != crcComputed) {
            return;
        }
        setSlaveData(slave);
    }

    /**
     * Returns the final bytes that the master should send after reading the
     * response from the slave. Defaults to ACK + SYN.
     */
    default byte[] getMasterFinalData() {
        return new byte[] { ACK, (byte) SYN };
    }

}
