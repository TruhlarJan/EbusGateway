package com.joiner.ebus.communication.protherm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.joiner.ebus.communication.EbusCrc;

class ProthermDataTest {

    @Test
    void simpleMasterDataConstructors_storePayloadKeyAndDate() {
        byte[] payload = new byte[] { 0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02, 0x00, 0x66 };

        Tg0315B513Data tg0315 = new Tg0315B513Data(payload);
        assertSame(payload, tg0315.getMasterData());
        assertEquals(Tg0315B513Data.KEY, tg0315.getKey());
        assertNotNull(tg0315.getDate());

        Tg0364B512Data tg0364 = new Tg0364B512Data(payload);
        assertSame(payload, tg0364.getMasterData());
        assertEquals(Tg0364B512Data.KEY, tg0364.getKey());
        assertNotNull(tg0364.getDate());

        TgUnknownData unknown = new TgUnknownData(payload);
        assertSame(payload, unknown.getMasterData());
        assertEquals(TgUnknownData.KEY, unknown.getKey());
        assertNotNull(unknown.getDate());
    }

    @Test
    void tg1008B510_customConstructor_setsConfiguredFieldsAndComputedCrc() {
        Tg1008B510Data telegram = new Tg1008B510Data(0x21, 0x43, 0x02);

        byte[] masterData = telegram.getMasterData();
        assertEquals(0x21, masterData[Tg1008B510Data.M8_INDEX] & 0xFF);
        assertEquals(0x43, masterData[Tg1008B510Data.M9_INDEX] & 0xFF);
        assertEquals(0x02, masterData[Tg1008B510Data.M12_INDEX] & 0xFF);
        assertEquals(EbusCrc.computeCrc(Arrays.copyOf(masterData, masterData.length - 1)), masterData[masterData.length - 1] & 0xFF);
        assertEquals(Tg1008B510Data.KEY, telegram.getKey());
        assertNotNull(telegram.getDate());
    }

    @Test
    void masterSlaveTelegramConstructors_parseValidCombinedFrames() {
        Tg1008B510Data setter = new Tg1008B510Data(concatWithComputedSlaveCrc(
                new Tg1008B510Data().getMasterData(),
                0x00, 0x01, 0x01));
        assertEquals(Tg1008B510Data.KEY, setter.getKey());
        assertArrayEquals(new byte[] { 0x00, 0x01, 0x01, (byte) EbusCrc.computeCrc(new byte[] { 0x00, 0x01, 0x01 }) }, setter.getSlaveData());
        assertNotNull(setter.getDate());

        Tg1008B5110100Data block0 = new Tg1008B5110100Data(concatWithComputedSlaveCrc(
                new Tg1008B5110100Data().getMasterData(),
                0x00, 0x08, 0x49, 0x02, 0x0C, 0x00, 0x1F, 0x10, 0x00, (byte) 0x80));
        assertEquals(Tg1008B5110100Data.KEY, block0.getKey());
        assertNotNull(block0.getDate());

        Tg1008B5110101Data block1 = new Tg1008B5110101Data(concatWithComputedSlaveCrc(
                new Tg1008B5110101Data().getMasterData(),
                0x00, 0x09, 0x20, 0x30, 0x01, 0x02, 0x40, 0x50, 0x60, 0x70, 0x71));
        assertEquals(Tg1008B5110101Data.KEY, block1.getKey());
        assertNotNull(block1.getDate());

        Tg1008B5110102Data block2 = new Tg1008B5110102Data(concatWithComputedSlaveCrc(
                new Tg1008B5110102Data().getMasterData(),
                0x00, 0x05, 0x11, 0x22, 0x33, 0x44, 0x55));
        assertEquals(Tg1008B5110102Data.KEY, block2.getKey());
        assertNotNull(block2.getDate());
    }

    @Test
    void masterSlaveParsing_rejectsNullLengthMismatchAndInvalidCrc() {
        assertThrows(IllegalArgumentException.class, () -> new Tg1008B510Data((byte[]) null));
        assertThrows(IllegalArgumentException.class, () -> new Tg1008B5110100Data(new byte[] { 0x01, 0x02 }));

        byte[] invalidCrc = concatWithComputedSlaveCrc(new Tg1008B5110101Data().getMasterData(),
                0x00, 0x09, 0x20, 0x30, 0x01, 0x02, 0x40, 0x50, 0x60, 0x70, 0x71);
        invalidCrc[invalidCrc.length - 1] ^= 0x01;

        assertThrows(IllegalStateException.class, () -> new Tg1008B5110101Data(invalidCrc));
    }

    private static byte[] concatWithComputedSlaveCrc(byte[] masterData, int... slaveWithoutCrc) {
        byte[] slave = new byte[slaveWithoutCrc.length + 1];
        for (int i = 0; i < slaveWithoutCrc.length; i++) {
            slave[i] = (byte) slaveWithoutCrc[i];
        }
        slave[slave.length - 1] = (byte) EbusCrc.computeCrc(Arrays.copyOf(slave, slave.length - 1));

        byte[] combined = Arrays.copyOf(masterData, masterData.length + slave.length);
        System.arraycopy(slave, 0, combined, masterData.length, slave.length);
        return combined;
    }
}