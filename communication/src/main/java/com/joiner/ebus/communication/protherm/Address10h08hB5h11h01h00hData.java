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
 * 3.6 B5h 11h 00h - Not specified.
 */
@Component
public class Address10h08hB5h11h01h00hData implements MasterSlaveData {

    public static final Long KEY = 17629583573248L;

    /* SB byte - Operational Data from Room Controller to Burner Control Unit. */
    private static final int SB = 0x11;
    
    /* NN byte - Length of data */
    private static final int NN = 0x01;

    /* M6 byte - Block number */
    private static final int M6 = 0x00;

    /* CRC = 0x89 */ 
    private static final int CRC = 0x88;

    /* Length of the slave data (ACK, NN, ??, ?, ?, ?, ?, ?, ?, ?, CRC) */
    private static final int SLAVE_SIZE = 11;

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
    public Address10h08hB5h11h01h00hData() {
        setMasterData(masterData);
    }

    /**
     * 
     * @param address
     * @param data
     */
    public Address10h08hB5h11h01h00hData(byte[] address, byte[] data) {
        setMasterSlaveData(address, data);
    }

    @Override
    public long getKey() {
        return KEY;
    }

}
