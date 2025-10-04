package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.AddressUnknownData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

@SpringBootTest
class FrameParserTest {

    @Autowired
    private FrameParser frameParser;

    @Test
    void testSave() {
        MasterSlaveData MasterSlaveData = frameParser.getMasterSlaveData(new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06});
        assertEquals(Address03h15hB5h13hData.KEY, MasterSlaveData.getKey());

        MasterSlaveData MasterSlaveData2 = frameParser.getMasterSlaveData(new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02});
        assertEquals(Address03h64hB5h12hData.KEY, MasterSlaveData2.getKey());

        MasterSlaveData MasterSlaveData3 = frameParser.getMasterSlaveData(new byte[6]);
        assertEquals(AddressUnknownData.KEY, MasterSlaveData3.getKey());

    }

}
