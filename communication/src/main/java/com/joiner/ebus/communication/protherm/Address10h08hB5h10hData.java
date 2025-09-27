package com.joiner.ebus.communication.protherm;

import java.io.ByteArrayOutputStream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * eBUS Specification
 * Application Layer – OSI 7
 * Vaillant specific extensions V0.6.0
 * 
 * 3.5 B5h 10h - Operational Data from Room Controller to Burner Control Unit
 */
@Slf4j
public class Address10h08hB5h10hData implements MasterSlaveData {
    
    /**
     * SB byte - Operational Data from Room Controller to Burner Control Unit.
     */
    public static final int SB_BYTE = 0x10;
    
    /** NN byte - Length of data in master M6 - M15
     * 
     */
    public static final int NN_BYTE = 0x09;

    /**
     * Length of the slave data (ACK, NN, ZZ, CRC)
     */
    private static final int SLAVE_SIZE = 4;

    private final ByteArrayOutputStream masterStream = new ByteArrayOutputStream();
    
    @Getter
    private byte[] slaveData;
    
    /**
     * Master byte M1 - M15
     * @param m8 M8 - Lead water target temperature
     * @param m9 M9 - Service water target temperature
     * @param m12 M12 - Burner blocking (00 = nothing blocked, 01 = lead water burner blocked, 02 = service water burner blocked, 05 = all blocked)
     */
    public Address10h08hB5h10hData(final int m8byte, final int m9byte, final int m12byte) {
        masterStream.write(QQ_BYTE);
        masterStream.write(ZZ_BYTE);
        masterStream.write(PB_BYTE);
        masterStream.write(SB_BYTE);
        masterStream.write(NN_BYTE);
        masterStream.write(UNKNOWN_00);
        masterStream.write(UNKNOWN_00);
        masterStream.write(m8byte);
        masterStream.write(m9byte);
        masterStream.write(UNKNOWN_FF);
        masterStream.write(UNKNOWN_FF);
        masterStream.write(m12byte);
        masterStream.write(UNKNOWN_FF);
        masterStream.write(UNKNOWN_00);
    }

    public byte[] getMasterCrcEndedData() {
        byte[] frame = masterStream.toByteArray();
        int crc = EbusCrc.computeCrc(frame);
        byte[] frameWithCrc = new byte[frame.length + 1];
        System.arraycopy(frame, 0, frameWithCrc, 0, frame.length);
        frameWithCrc[frame.length] = (byte) crc;
        return frameWithCrc;
    }
    
    @Override
    public int getSlaveSize() {
        return SLAVE_SIZE;
    }

    @Override
    public void setSlaveData(byte[] response) {
        byte[] responseWithOutCrc = new byte[response.length - 1];
        System.arraycopy(response, 0, responseWithOutCrc, 0, responseWithOutCrc.length);
        int crcResponsed = response[responseWithOutCrc.length] & 0xFF;
        int crcComputed = EbusCrc.computeCrc(responseWithOutCrc);
        if (crcResponsed != crcComputed) {
            log.info("CRC responsed {} != CRC computed {}", crcResponsed, crcComputed); 
        }
        slaveData = response;
    }

}
