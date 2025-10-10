/**
 * 
 */
package com.joiner.ebus.communication.link;

import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;

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
    void testSendFrame() {
        
        List<MasterSlaveData> list = List.of(
                new Address10h08hB5h10hData(0x14, 0x5A, 0x05),
                new Address10h08hB5h11h01h00hData(),
                new Address10h08hB5h11h01h01hData(),
                new Address10h08hB5h11h01h02hData());

        assertThatNoException().isThrownBy(() -> {
            for (MasterSlaveData masterSlaveData : list) {
                dataSender.sendFrame(masterSlaveData);
            }
        });
    }

}
