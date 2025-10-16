package com.joiner.ebus.communication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.EbusWriter;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCollector {

    @Value("${collector.poller.enabled:true}")
    private boolean pollerEnabled;

    @Value("${collector.setting.enabled:true}")
    private boolean settingEnabled;
    
    @Value("${collector.iteration.delay:2000}")
    private long schedulerDelay;

    @Autowired
    private EbusWriter ebusMasterSlaveLink;

    private MasterSlaveData masterSlaveData = new Tg1008B510Data();
    private List<MasterSlaveData> masterSlaveDataList = List.of(new Tg1008B5110100Data(), new Tg1008B5110101Data(), new Tg1008B5110102Data());
    
    @Scheduled(fixedRateString = "${collector.scheduler.rate:10000}")
    public void sendData() {
        if (!pollerEnabled) {
            return;
        }

        List<MasterSlaveData> list = new ArrayList<>(masterSlaveDataList); 
        if (settingEnabled) {
            list.add(0, masterSlaveData);
        }

        for (MasterSlaveData masterSlaveData : list) {
            try {
                ebusMasterSlaveLink.sendFrame(masterSlaveData);
                Thread.sleep(schedulerDelay);
            } catch (Exception e) {
                log.error("Ebus MasterToSlave communication failed.", e);
            }
        }
    } 

    /**
     * Setting MasterSlaveData.
     * @param masterSlaveData (Tg1008B510Data) telegram.
     */
    public void sendDataImmidiately(MasterSlaveData masterSlaveData) {
        this.masterSlaveData = masterSlaveData;
    }

}
