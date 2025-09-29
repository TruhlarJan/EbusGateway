package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.EbusMockServerApplication;
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h10hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h02hData;

@SpringBootTest(classes = EbusMockServerApplication.class)
class FrameParserTest {

    @Autowired
    private FrameParser frameParser;

    @Test
    void testSave() {
        byte[] address03h15hB5h13hBytes = new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06};
        assertEquals(Address03h15hB5h13hData.KEY, frameParser.save(address03h15hB5h13hBytes));

        byte[] address03h64hB5h12hBytes = new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02};
        assertEquals(Address03h64hB5h12hData.KEY, frameParser.save(address03h64hB5h12hBytes));

        byte[] address10h08hB5h10hByte = new Address10h08hB5h10hData(0x00, 0x00, 0x00).getMasterStartData();
        assertEquals(Address10h08hB5h10hData.KEY, frameParser.save(address10h08hB5h10hByte));

        byte[] address10h08hB5h11h00hByte = new Address10h08hB5h11h00hData().getMasterStartData();
        assertEquals(Address10h08hB5h11h00hData.KEY, frameParser.save(address10h08hB5h11h00hByte));

        byte[] address10h08hB5h11h01hByte = new Address10h08hB5h11h01hData().getMasterStartData();
        assertEquals(Address10h08hB5h11h01hData.KEY, frameParser.save(address10h08hB5h11h01hByte));

        byte[] address10h08hB5h11h02hByte = new Address10h08hB5h11h02hData().getMasterStartData();
        assertEquals(Address10h08hB5h11h02hData.KEY, frameParser.save(address10h08hB5h11h02hByte));

    }

    @Test
    void testBytesToLong() {
        assertEquals(4328719365L, frameParser.getKey(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05}));
    }

    @Test
    void testGetMap() {
        frameParser.save(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05});
        assertArrayEquals(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05}, frameParser.getMap().get(4328719365L));
    }

}
