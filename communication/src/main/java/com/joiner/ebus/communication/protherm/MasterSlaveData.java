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
     * Set slave data to the object.
     * @param slaveData
     */
    void setSlaveData(byte[] slaveData);

    /**
     * Parsing slave data before setting it to the object.
     * @param slaveData
     */
    default void parseMasterSlaveData(byte[] masterSlaveData) {
        if (masterSlaveData == null) {
            throw new IllegalArgumentException("masterSlaveData must not be null");
        }

        if (getMasterData().length + getSlaveData().length != masterSlaveData.length) {
            throw new IllegalArgumentException("Length mismatch: expected " + (getMasterData().length + getSlaveData().length) + " but got " + masterSlaveData.length
            );
        }

        byte[] master = Arrays.copyOfRange(masterSlaveData, 0, getMasterData().length);
        setMasterData(master);
        
        byte[] slave = Arrays.copyOfRange(masterSlaveData, getMasterData().length, masterSlaveData.length);
        int crcResponsed = slave[slave.length - 1] & 0xFF;
        int crcComputed = EbusCrc.computeCrc(Arrays.copyOf(slave, slave.length - 1)) & 0xFF;
        if (crcResponsed != crcComputed) {
            throw new IllegalStateException(String.format("CRC mismatch: expected 0x%02X but got 0x%02X", crcComputed, crcResponsed));
        }
        setSlaveData(slave);
    }

}
