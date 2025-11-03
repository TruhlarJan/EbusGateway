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
        if (masterSlaveData == null) {
            throw new IllegalArgumentException("masterSlaveData must not be null");
        }

        if (getMasterSlaveData().length != masterSlaveData.length) {
            throw new IllegalArgumentException("Length mismatch: expected " + getMasterSlaveData().length + " but got " + masterSlaveData.length
            );
        }
        byte[] slave = Arrays.copyOfRange(masterSlaveData, getMasterData().length, masterSlaveData.length);
        if (slave.length < 2) { // at least 1 byte of data + 1 byte CRC
            throw new IllegalArgumentException("Slave data too short to contain CRC");
        }
        int crcResponsed = slave[slave.length - 1] & 0xFF;
        int crcComputed = EbusCrc.computeCrc(Arrays.copyOf(slave, slave.length - 1)) & 0xFF;
        if (crcResponsed != crcComputed) {
            throw new IllegalStateException(String.format("CRC mismatch: expected 0x%02X but got 0x%02X", crcComputed, crcResponsed));
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
