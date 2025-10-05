package com.joiner.ebus.communication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.EbusWriter;
import com.joiner.ebus.communication.protherm.Address10h08hB5h10hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCollector {

    @Value("${collector.poller.enabled:true}")
    private boolean pollerEnabled;

    @Value("${collector.iteration.delay:2000}")
    private long schedulerDelay;

    @Autowired
    private EbusWriter ebusMasterSlaveLink;

    /* Default value M8 = 14 */
    private int m8 = 0x14;
    /* Default value M9 = 0x5A */
    private int m9 = 0x5A;
    /* Default value M12 = 0x01 */
    private int m12 = 0x05;

    @Scheduled(fixedRateString = "${collector.scheduler.rate:10000}")
    public void sendData() {
        if (!pollerEnabled) {
            return;
        }

        List<MasterSlaveData> list = List.of(
                new Address10h08hB5h10hData(m8, m9, m12),
                new Address10h08hB5h11h01h00hData(),
                new Address10h08hB5h11h01h01hData(),
                new Address10h08hB5h11h01h02hData());

        for (MasterSlaveData masterSlaveData : list) {
            try {
                ebusMasterSlaveLink.sendFrame(masterSlaveData);
                Thread.sleep(schedulerDelay);
            } catch (Exception e) {
                log.error("Ebus MasterToSlave communication failed.", e);
            }
        }
    } 

    public void sendDataImmidiately(MasterSlaveData masterSlaveData) {
        byte[] masterStartData = masterSlaveData.getMasterData();
        m8 = masterStartData[Address10h08hB5h10hData.M8_INDEX];
        m9 = masterStartData[Address10h08hB5h10hData.M9_INDEX];
        m12 = masterStartData[Address10h08hB5h10hData.M12_INDEX];
    }

}
