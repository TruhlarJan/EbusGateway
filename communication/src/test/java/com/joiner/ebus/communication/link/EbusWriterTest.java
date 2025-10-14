/**
 * 
 */
package com.joiner.ebus.communication.link;

import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
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
                new Tg1008B510Data(0x14, 0x5A, 0x05),
                new Tg1008B5110100Data(),
                new Tg1008B5110101Data(),
                new Tg1008B5110102Data());

        assertThatNoException().isThrownBy(() -> {
            for (MasterSlaveData masterSlaveData : list) {
                dataSender.sendFrame(masterSlaveData);
            }
        });
    }

}
