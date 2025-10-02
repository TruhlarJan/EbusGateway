package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.EbusMockServerApplication;
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.AddressUnknownData;
import com.joiner.ebus.communication.protherm.SlaveData;

@SpringBootTest(classes = EbusMockServerApplication.class)
class FrameParserTest {

    @Autowired
    private FrameParser frameParser;

    @Test
    void testSave() {
        SlaveData slaveData = frameParser.getSlaveData(new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06});
        assertEquals(Address03h15hB5h13hData.KEY, slaveData.getKey());

        SlaveData slaveData2 = frameParser.getSlaveData(new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02});
        assertEquals(Address03h64hB5h12hData.KEY, slaveData2.getKey());

        SlaveData slaveData3 = frameParser.getSlaveData(new byte[6]);
        assertEquals(AddressUnknownData.KEY, slaveData3.getKey());

    }

    @Test
    void testBytesToLong() {
        assertEquals(4328719365L, frameParser.getKey(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05}));
    }

}
