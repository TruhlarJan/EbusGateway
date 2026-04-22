package com.joiner.ebus.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class EbusCrcTests {

    @ParameterizedTest(name = "{index}: frame {0} -> CRC 0x{2}")
    @MethodSource("crcFrames")
    void computeCrcMatchesReferenceFrames(String description, byte[] frame, int expectedCrc) {
        assertEquals(expectedCrc, EbusCrc.computeCrc(frame));
    }

    private static Stream<Arguments> crcFrames() {
        return Stream.of(
                Arguments.of("03 15 B5 13 03 06 64 00", new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06, 0x64, 0x00}, 0x63),
                Arguments.of("03 15 B5 13 03 06 00 00", new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06, 0x00, 0x00}, 0x0E),
                Arguments.of("03 64 B5 12 02 02 00", new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02, 0x00}, 0x66),
                Arguments.of("03 64 B5 12 02 02 FE", new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02, (byte) 0xFE}, 0x98),
                Arguments.of("10 08 B5 13 02 05 08", new byte[] {0x10, 0x08, (byte) 0xB5, 0x13, 0x02, 0x05, 0x08}, 0xC6),
                Arguments.of("10 08 B5 10 09 00 00 1E 78 FF FF 01 FF 00",
                        new byte[] {0x10, 0x08, (byte) 0xB5, 0x10, 0x09, 0x00, 0x00, 0x1E, 0x78, (byte) 0xFF, (byte) 0xFF, 0x01, (byte) 0xFF, 0x00},
                        0x7A),
                Arguments.of("10 08 B5 11 01 00", new byte[] {0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x00}, 0x88),
                Arguments.of("10 08 B5 11 01 01", new byte[] {0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x01}, 0x89),
                Arguments.of("10 08 B5 11 01 02", new byte[] {0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x02}, 0x8A)
        );
    }
}



