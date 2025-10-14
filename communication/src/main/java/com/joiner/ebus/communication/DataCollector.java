package com.joiner.ebus.communication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.EbusWriter;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
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
                new Tg1008B510Data(m8, m9, m12),
                new Tg1008B5110100Data(),
                new Tg1008B5110101Data(),
                new Tg1008B5110102Data());

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
        m8 = masterStartData[Tg1008B510Data.M8_INDEX];
        m9 = masterStartData[Tg1008B510Data.M9_INDEX];
        m12 = masterStartData[Tg1008B510Data.M12_INDEX];
    }

}
