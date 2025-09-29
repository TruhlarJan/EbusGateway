package com.joiner.ebus.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EbusCrcTests {

    /**
     * Frame: 03 15 B5 13 03 06 64 00 -> CRC = 0x63
     */
    @Test
    void testFrame1() {
        byte[] frame = {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06, 0x64, 0x00};
        assertEquals(0x63, EbusCrc.computeCrc(frame));
    }

    /**
     * Frame: 03 15 B5 13 03 06 00 00 -> CRC = 0x0E
     */
    @Test
    void testFrame2() {
        byte[] frame = {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06, 0x00, 0x00};
        assertEquals(0x0E, EbusCrc.computeCrc(frame));
    }

    /**
     * Frame: 03 64 B5 12 02 02 00 -> CRC = 0x66
     */
    @Test
    void testFrame3() {
        byte[] frame = {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02, 0x00};
        assertEquals(0x66, EbusCrc.computeCrc(frame));
    }

    /**
     * Frame: 03 64 B5 12 02 02 FE -> CRC = 0x98
     */
    @Test
    void testFrame4() {
        byte[] frame = {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02, (byte) 0xFE};
        assertEquals(0x98, EbusCrc.computeCrc(frame));
    }

    /**
     * Frame: 10 08 B5 13 02 05 08 -> CRC = 0xC6
     */
    @Test
    void testFrame5() {
        byte[] frame = {0x10, 0x08, (byte) 0xB5, 0x13, 0x02, 0x05, 0x08};
        assertEquals(0xC6, EbusCrc.computeCrc(frame));
    }

    /**
     * Frame: 10 08 B5 10 09 00 00 1E 78 FF FF 01 FF 00 -> CRC = 0x7A
     */
    @Test
    void testFrame6() {
        byte[] frame = {0x10, 0x08, (byte) 0xB5, 0x10, 0x09, 0x00, 0x00, 0x1E, 0x78, (byte) 0xFF, (byte) 0xFF, 0x01, (byte) 0xFF, 0x00};
        assertEquals(0x7A, EbusCrc.computeCrc(frame));
    }

    /**
     * Frame: 10 08 B5 11 01 00 -> CRC = 0x89
     */
    @Test
    void testFrame7() {
        byte[] frame = {0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x00};
        assertEquals(0x88, EbusCrc.computeCrc(frame));
    }
    
    /**
     * Frame: 10 08 B5 11 01 01 -> CRC = 0x89
     */
    @Test
    void testFrame8() {
        byte[] frame = {0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x01};
        assertEquals(0x89, EbusCrc.computeCrc(frame));
    }

    /**
     * Frame: 10 08 B5 11 01 02 -> CRC = 0x8A
     */
    @Test
    void testFrame9() {
        byte[] frame = {0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x02};
        assertEquals(0x8A, EbusCrc.computeCrc(frame));
    }
}

