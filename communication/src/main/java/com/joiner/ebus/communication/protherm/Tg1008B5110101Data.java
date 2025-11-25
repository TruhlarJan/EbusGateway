package com.joiner.ebus.communication.protherm;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * eBUS Specification
 * Application Layer – OSI 7
 * Vaillant specific extensions V0.6.0
 * 
 * 3.6 B5h 11h 01h - Operational Data of Burner Control Unit to Room Control Unit.
 */
@Component
public class Tg1008B5110101Data implements MasterSlaveData {

    public static final Long KEY = 17629583573249L;

    /* SB byte - Operational Data from Room Controller to Burner Control Unit. */
    private static final int SB = 0x11;
    
    /* NN byte - Length of data */
    private static final int NN = 0x01;

    /* M6 byte - Block number */
    private static final int M6 = 0x01;

    /* CRC = 0x89 */ 
    private static final int CRC = 0x89;

    /* Length of the slave data (ACK, NN, VT, NT, TA_L, TA_H, WT, ST, vv, xx1, xx2, CRC) */
    private static final int SLAVE_SIZE = 12;
    public static final int VT_INDEX = 2;
    public static final int NT_INDEX = 3;
    public static final int ST_INDEX = 7;
    public static final int VV_INDEX = 8;

    /* 10h 08h B5h 11h 01h 01h*/
    @Getter
    @Setter
    private byte[] masterData = new byte[] {QQ, ZZ, (byte) PB, SB, NN, M6, (byte) CRC};

    @Getter
    @Setter
    private byte[] slaveData = new byte[SLAVE_SIZE];
    
    @Getter
    @Setter
    private Date date;

    /**
     * 
     */
    public Tg1008B5110101Data() {
        setMasterData(masterData);
        setDate(new Date());
    }

    /**
     * 
     * @param data
     */
    public Tg1008B5110101Data(byte[] masterSlaveData) {
        parseMasterSlaveData(masterSlaveData);
        setDate(new Date());
    }

    @Override
    public long getKey() {
        return KEY;
    }

}
