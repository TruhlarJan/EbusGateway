package com.joiner.ebus.communication;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JohnEbusCrcTests {

    @Test
    void computeCrcMatchesKnownReferenceFrames() {
        assertEquals(0x63, JohnEbusCrc.computeCrc(hex("03 15 B5 13 03 06 64 00")));
        assertEquals(0x0E, JohnEbusCrc.computeCrc(hex("03 15 B5 13 03 06 00 00")));
        assertEquals(0x66, JohnEbusCrc.computeCrc(hex("03 64 B5 12 02 02 00")));
        assertEquals(0x98, JohnEbusCrc.computeCrc(hex("03 64 B5 12 02 02 FE")));
        assertEquals(0xC6, JohnEbusCrc.computeCrc(hex("10 08 B5 13 02 05 08")));
        assertEquals(0x7A, JohnEbusCrc.computeCrc(hex("10 08 B5 10 09 00 00 1E 78 FF FF 01 FF 00")));
        assertEquals(0x89, JohnEbusCrc.computeCrc(hex("10 08 B5 11 01 01")));
        assertEquals(0x8A, JohnEbusCrc.computeCrc(hex("10 08 B5 11 01 02")));
    }

    @Test
    void computeCrcEscapedMatchesExpandedEscapedPayload() {
        byte[] data = hex("A9 AA 12 34 A9 55 AA");
        byte[] escapedData = hex("A9 00 A9 01 12 34 A9 00 55 A9 01");

        assertEquals(JohnEbusCrc.computeCrc(escapedData), JohnEbusCrc.computeCrcEscaped(data));
    }

    @Test
    void computeCrcEscapedMatchesComputeCrcWhenNoEscapingIsNeeded() {
        byte[] data = hex("10 08 B5 13 02 05 08");

        assertEquals(JohnEbusCrc.computeCrc(data), JohnEbusCrc.computeCrcEscaped(data));
    }

    @Test
    void hexStringToBytesIgnoresWhitespace() {
        assertArrayEquals(
                new byte[] { 0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06, 0x64, 0x00 },
                JohnEbusCrc.hexStringToBytes("03 15\nB5\t13 03 06 64 00"));
    }

    @Test
    void emptyPayloadProducesZeroCrc() {
        assertEquals(0, JohnEbusCrc.computeCrc(new byte[0]));
        assertEquals(0, JohnEbusCrc.computeCrcEscaped(new byte[0]));
    }

    private byte[] hex(String value) {
        return JohnEbusCrc.hexStringToBytes(value);
    }
}