package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.EbusMockServerApplication;
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;

@SpringBootTest(classes = EbusMockServerApplication.class)
class FrameParserTest {

    @Autowired
    private FrameParser frameParser;

    @Test
    void testSave() {
        byte[] bytes = new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06, 0x64, 0x00, 0x63};
        assertEquals(Address03h15hB5h13hData.KYE, frameParser.save(bytes));
    }

    @Test
    void testBytesToLong() {
        assertEquals(16909060L, frameParser.bytesToLong(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04}));
    }

    @Test
    void testLongToBytes() {
        assertArrayEquals(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04}, frameParser.longToBytes(16909060L));
    }

    @Test
    void testGetMap() {
        frameParser.save(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04});
        assertArrayEquals(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04}, frameParser.getMap().get(16909060L));
    }

}
