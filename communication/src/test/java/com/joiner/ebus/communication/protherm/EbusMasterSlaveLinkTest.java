/**
 * 
 */
package com.joiner.ebus.communication.protherm;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.EbusMockServerApplication;
import com.joiner.ebus.communication.EbusMasterSlaveLink;
import com.joiner.ebus.communication.MasterSlaveData;
import com.joiner.ebus.io.mock.MasterSlaveMockServer;

/**
 * 
 */
@SpringBootTest(classes = EbusMockServerApplication.class)
class EbusMasterSlaveLinkTest {

    @Autowired
    private EbusMasterSlaveLink dataSender;

    @Autowired
    private MasterSlaveMockServer masterSlaveMockServer;

    /**
     * Test method for {@link com.joiner.ebus.communication.EbusMasterSlaveLink#sendFrame(com.joiner.ebus.communication.MasterSlaveData)}.
     * @throws Exception 
     */
    @Test
    void testSendFrame() throws Exception {
        MasterSlaveData operationalData = new Address10h08hB5h10hData(0x3E, 0x5A, 0x01);
        byte[] echoMasterFrame = dataSender.sendFrame(operationalData);

        // 10 08 B5 10 09 00 00 3E 5A FF FF 01 FF 00 1D
        byte[] masterCrcEnded = new byte[]{0x10, 0x08, (byte) 0xB5, 0x10, 0x09, 0x00, 0x00, 0x3E, 0x5A, (byte) 0xFF, (byte) 0xFF, 0x01, (byte) 0xFF, 0x00, 0x1D};
        assertArrayEquals(masterCrcEnded, operationalData.getMasterCrcEndedData());
        assertArrayEquals(masterCrcEnded, echoMasterFrame);

        // 00 01 01 9A
        byte[] slaveData = new byte[]{0x00, 0x01, 0x01, (byte) 0x9A};
        assertArrayEquals(slaveData, operationalData.getSlaveData());

        // 00 AA
        byte[] finalData = new byte[]{0x00, (byte) 0xAA};
        assertArrayEquals(finalData, operationalData.getMasterFinalData());
        
        masterSlaveMockServer.stop();
    }

}
