package com.joiner.ebus.communication.protherm;

import java.util.Arrays;

import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.EbusCrc;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * eBUS Specification
 * Application Layer – OSI 7
 * Vaillant specific extensions V0.6.0
 * 
 * 3.6 B5h 11h 01h - Operational Data of Burner Control Unit to Room Control Unit.
 */
@Component
@Slf4j
public class Address10h08hB5h11h01hData implements MasterSlaveData {

    public static final Long KEY = 17629583507713L;

    /* SB byte - Operational Data from Room Controller to Burner Control Unit. */
    private static final int SB = 0x10;
    
    /* NN byte - Length of data */
    private static final int NN = 0x01;

    /* M6 byte - Block number */
    private static final int M6 = 0x01;

    /* CRC = 0x89 */ 
    private static final int CRC = 0x89;

    /* 10h 08h B5h 11h 01h 01h*/
    private final byte[] getMasterData = new byte[] {QQ, ZZ, (byte) PB, SB, NN, M6, (byte) CRC};

    /* Length of the slave data (ACK, NN, VT, NT, TA_L, TA_H, WT, ST, vv, xx1, xx2, CRC) */
    private static final int SLAVE_SIZE = 12;

    @Getter
    private byte[] slaveData;

    @Override
    public byte[] getMasterStartData() {
        return getMasterData;
    }

    @Override
    public int getSlaveSize() {
        return SLAVE_SIZE;
    }

    @Override
    public void setSlaveData(byte[] response) {
        int crcResponsed = response[response.length - 1];
        int crcComputed = EbusCrc.computeCrc(Arrays.copyOf(response, response.length - 1));
        if (crcResponsed != crcComputed) {
            log.info("CRC responsed {} != CRC computed {}", crcResponsed, crcComputed); 
        }
        slaveData = response;
    }

}
