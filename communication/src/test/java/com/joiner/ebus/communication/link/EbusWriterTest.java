/**
 * 
 */
package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.communication.protherm.Address10h08hB5h10hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

/**
 * 
 */
@SpringBootTest
class EbusWriterTest {

    @Autowired
    private EbusWriter dataSender;

    /**
     * Test method for {@link com.joiner.ebus.communication.link.EbusWriter#sendFrame(com.joiner.ebus.communication.protherm.MasterSlaveData)}.
     * @throws Exception 
     */
    @Test
    void testSendFrame10h08hB5h10h() throws Exception {
        MasterSlaveData operationalData = new Address10h08hB5h10hData(0x3E, 0x5A, 0x01);
        dataSender.sendFrame(operationalData);
        // 10 08 B5 10 09 00 00 3E 5A FF FF 01 FF 00 1D
        byte[] masterCrcEnded = new byte[]{0x10, 0x08, (byte) 0xB5, 0x10, 0x09, 0x00, 0x00, 0x3E, 0x5A, (byte) 0xFF, (byte) 0xFF, 0x01, (byte) 0xFF, 0x00, 0x1D};
        assertArrayEquals(masterCrcEnded, operationalData.getMasterData());
        // 00 01 01 9A
        byte[] slaveData = new byte[]{0x00, 0x01, 0x01, (byte) 0x9A};
        assertArrayEquals(slaveData, operationalData.getSlaveData());
        // 00 AA
        byte[] finalData = new byte[]{0x00, (byte) 0xAA};
        assertArrayEquals(finalData, operationalData.getMasterFinalData());
    }

    /**
     * Test method for {@link com.joiner.ebus.communication.link.EbusWriter#sendFrame(com.joiner.ebus.communication.protherm.MasterSlaveData)}.
     * @throws Exception 
     */
//    @Test
    void testSendFrame10h08hB5h11h01h00h() throws Exception {
        MasterSlaveData operationalData = new Address10h08hB5h11h01h00hData();
        dataSender.sendFrame(operationalData);
        // 10 08 B5 11 01 00 88
        byte[] masterCrcEnded = new byte[]{0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x00, (byte) 0x88};
        assertArrayEquals(masterCrcEnded, operationalData.getMasterData());
        // 00 08 50 02 0c 00 1f 10 00 80 07
        byte[] slaveData = new byte[]{0x00, 0x08, 0x50, 0x02, 0x0C, 0x00, 0x1F, 0x10, 0x00, (byte) 0x80, (byte) 0x07};
        assertArrayEquals(slaveData, operationalData.getSlaveData());
        // 00 AA
        byte[] finalData = new byte[]{0x00, (byte) 0xAA};
        assertArrayEquals(finalData, operationalData.getMasterFinalData());
    }

    /**
     * Test method for {@link com.joiner.ebus.communication.link.EbusWriter#sendFrame(com.joiner.ebus.communication.protherm.MasterSlaveData)}.
     * @throws Exception 
     */
    @Test
    void testSendFrame10h08hB5h11h01h01h() throws Exception {
        MasterSlaveData operationalData = new Address10h08hB5h11h01h01hData();
        dataSender.sendFrame(operationalData);
        // 10 08 B5 11 01 01 89
        byte[] masterCrcEnded = new byte[]{0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x01, (byte) 0x89};
        assertArrayEquals(masterCrcEnded, operationalData.getMasterData());
        // 00 09 4a 46 00 80 ff 5c 00 00 ff B0
        byte[] slaveData = new byte[]{0x00, 0x09, 0x4a, 0x46, 0x00, (byte) 0x80, (byte) 0xFF, 0x5c, 0x00, 0x00, (byte) 0xFF, (byte) 0xB0};
        assertArrayEquals(slaveData, operationalData.getSlaveData());
        // 00 AA
        byte[] finalData = new byte[]{0x00, (byte) 0xAA};
        assertArrayEquals(finalData, operationalData.getMasterFinalData());
    }

    /**
     * Test method for {@link com.joiner.ebus.communication.link.EbusWriter#sendFrame(com.joiner.ebus.communication.protherm.MasterSlaveData)}.
     * @throws Exception 
     */
//    @Test
    void testSendFrame10h08hB5h11h01h02h() throws Exception {
        MasterSlaveData operationalData = new Address10h08hB5h11h01h02hData();
        dataSender.sendFrame(operationalData);
        // 10 08 B5 11 01 02 8A
        byte[] masterCrcEnded = new byte[]{0x10, 0x08, (byte) 0xB5, 0x11, 0x01, 0x02, (byte) 0x8A};
        assertArrayEquals(masterCrcEnded, operationalData.getMasterData());
        // 00 05 02 14 96 5a 78 0D
        byte[] slaveData = new byte[]{0x00, 0x05, 0x02, 0x14, (byte) 0x96, 0x5A, 0x78, 0x0D};
        assertArrayEquals(slaveData, operationalData.getSlaveData());
        // 00 AA
        byte[] finalData = new byte[]{0x00, (byte) 0xAA};
        assertArrayEquals(finalData, operationalData.getMasterFinalData());
    }

}
