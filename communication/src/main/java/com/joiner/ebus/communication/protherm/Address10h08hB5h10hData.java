package com.joiner.ebus.communication.protherm;

import java.util.Arrays;
import java.util.Date;

import com.joiner.ebus.communication.EbusCrc;

import lombok.Getter;
import lombok.Setter;


/**
 * eBUS Specification
 * Application Layer – OSI 7
 * Vaillant specific extensions V0.6.0
 * 
 * 3.5 B5h 10h - Operational Data from Room Controller to Burner Control Unit
 */
public class Address10h08hB5h10hData implements MasterSlaveData {
    
    public static final Long KEY = 17629583509760L;
    
    /* SB byte - Operational Data from Room Controller to Burner Control Unit. */
    private static final int SB = 0x10;
    
    /* NN byte - Length of data in master M6 - M15 */
    private static final int NN = 0x09;

    /* Default value M8 = 14 */
    private static final int M8 = 0x14;
    public static final int M8_INDEX = 7;

    /* Default value M9 = 0x5A */
    private static final int M9 = 0x5A;
    public static final int M9_INDEX = 8;

    /* Default value M12 = 0x01 */
    private static final int M12 = 0x05;
    public static final int M12_INDEX = 11;
    
    /* Default value CRC = 0x47 */ 
    private static final int CRC = 0x47;
    private static final int CRC_INDEX = 14;

    /* Length of the slave data (ACK, NN, ZZ, CRC) */
    private static final int SLAVE_SIZE = 4;
    
    /* 10h 08h B5h 10h 09h*/
    @Getter
    @Setter
    private byte[] masterData = new byte[] {QQ, ZZ, (byte) PB, SB, NN, 0x00, 0x00, M8, M9, (byte) 0xFF, (byte) 0xFF, M12, (byte) 0xFF, 0x00, CRC};

    /* ACK, NN, ZZ, CRC */
    @Getter
    @Setter
    private byte[] slaveData = new byte[SLAVE_SIZE];
    
    @Getter
    @Setter
    private Date date;
  
    /**
     * Master byte M1 - M15
     * @param m8 M8 - Lead water target temperature
     * @param m9 M9 - Service water target temperature
     * @param m12 M12 - Burner blocking (00 = nothing blocked, 01 = lead water burner blocked, 02 = service water burner blocked, 05 = all blocked)
     */
    public Address10h08hB5h10hData(final int m8byte, final int m9byte, final int m12byte) {
        masterData[M8_INDEX] = (byte) m8byte;
        masterData[M9_INDEX] = (byte) m9byte;
        masterData[M12_INDEX] = (byte) m12byte;
        masterData[CRC_INDEX] = (byte) EbusCrc.computeCrc(Arrays.copyOf(masterData, masterData.length - 1));
        setMasterData(masterData);
    }

    /**
     * 
     * @param address
     * @param data
     */
    public Address10h08hB5h10hData(byte[] address, byte[] data) {
        setMasterSlaveData(address, data);
    }

    @Override
    public long getKey() {
        return KEY;
    }
}
