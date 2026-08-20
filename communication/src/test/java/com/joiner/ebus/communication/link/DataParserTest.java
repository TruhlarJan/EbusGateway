package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.TgUnknownData;

class DataParserTest {

    private static DataParser newParser() {
        DataParser parser = new DataParser();
        ReflectionTestUtils.setField(parser, "utils", new ByteUtils());
        return parser;
    }

    @Test
    void getMasterSlaveData_returnsConcreteTelegram_forSupportedKeys() {
        DataParser parser = newParser();

        // 10 08 B5 10 ... + slave part
        byte[] tg1008b510 = hexToBytes(
                "10 08 B5 10 09 00 00 14 5A FF FF 05 FF 00 47 00 01 01 9A");

        MasterSlaveData parsed1 = parser.getMasterSlaveData(tg1008b510);

        assertInstanceOf(Tg1008B510Data.class, parsed1);

        // 10 08 B5 11 01 00 ... + slave part
        byte[] tg1008b5110100 = hexToBytes(
                "10 08 B5 11 01 00 88 00 08 49 02 0C 00 1F 10 00 80 2E");

        MasterSlaveData parsed2 = parser.getMasterSlaveData(tg1008b5110100);

        assertInstanceOf(Tg1008B5110100Data.class, parsed2);
    }

    @Test
    void getMasterSlaveData_returnsNull_forUnknownOrInvalidInput() {
        DataParser parser = newParser();

        // Unknown key (but long enough for key extraction)
        assertNull(parser.getMasterSlaveData(
                hexToBytes("01 02 03 04 05 06")));

        // Too short => ByteUtils throws => parser catches and returns null
        assertNull(parser.getMasterSlaveData(
                new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05 }));
    }

    @Test
    void getMasterData_returnsConcreteTelegram_forSupportedKey() {
        DataParser parser = newParser();

        byte[] tg0364 = hexToBytes("03 64 B5 12 02 02 00 66");

        MasterData parsed = parser.getMasterData(tg0364);

        assertInstanceOf(Tg0364B512Data.class, parsed);
    }

    @Test
    void getMasterData_returnsUnknownTelegram_forUnknownKey() {
        DataParser parser = newParser();

        byte[] unknown = hexToBytes("04 04 04 04 04 04");

        MasterData parsed = parser.getMasterData(unknown);

        assertInstanceOf(TgUnknownData.class, parsed);
        assertEquals(6, ((TgUnknownData) parsed).getMasterData().length);
    }

    @Test
    void getMasterData_returnsUnknownTelegram_forShortTelegram() {
        DataParser parser = newParser();

        // Telegram shorter than 6 bytes cannot have a key,
        // but it must not be silently discarded.
        byte[] shortTelegram = hexToBytes("03 64 B5 12");

        MasterData parsed = parser.getMasterData(shortTelegram);

        assertInstanceOf(TgUnknownData.class, parsed);
        assertEquals(
                shortTelegram.length,
                ((TgUnknownData) parsed).getMasterData().length);
    }

    private static byte[] hexToBytes(String hex) {
        String[] tokens = hex.trim().split("\\s+");
        byte[] out = new byte[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            out[i] = (byte) Integer.parseInt(tokens[i], 16);
        }

        return out;
    }
}