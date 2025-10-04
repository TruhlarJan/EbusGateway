package com.joiner.ebus.communication.protherm;

import java.util.Arrays;
import java.util.Date;

import com.joiner.ebus.communication.EbusCrc;

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

    /**
     * Address key of the MasterSlave object.
     * 
     * @return
     */
    long getKey();

    /**
     * Returns the final bytes that the master should send after reading the
     * response from the slave. Defaults to ACK + SYN.
     */
    default byte[] getMasterFinalData() {
        return new byte[] { ACK, (byte) SYN };
    }

    /**
     * 
     * @param data
     */
    default void setMasterSlaveData(byte[] data) {
        if (getMasterData().length <= data.length) {
            setMasterData(Arrays.copyOfRange(data, 0, getMasterData().length));
        }
        if (data.length > getMasterData().length) {
            byte[] slave = Arrays.copyOfRange(data, getMasterData().length, data.length);
            int crcResponsed = slave[slave.length - 1] & 0xFF;
            int crcComputed = EbusCrc.computeCrc(Arrays.copyOf(slave, slave.length - 1)) & 0xFF;
            if (crcResponsed == crcComputed) {
                setSlaveData(slave);
                setDate(new Date());
            }
        }
    }

}
