package com.joiner.ebus.communication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.EbusReaderWriter;
import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCollector {

    @Value("${collector.setter.enabled:true}")
    private boolean setterEnabled;

    @Value("${collector.getter.enabled:true}")
    private boolean getterEnabled;

    @Value("${collector.iteration.delay:2000}")
    private long schedulerDelay;

    @Autowired
    private EbusReaderWriter ebusReaderWriter;

    @Getter
    private volatile Tg1008B510Data tg1008B510Data =
            new Tg1008B510Data();

    private final List<MasterSlaveData> masterSlaveDataList =
            List.of(
                    new Tg1008B5110100Data(),
                    new Tg1008B5110101Data(),
                    new Tg1008B5110102Data()
            );

    @Scheduled(fixedRateString = "${collector.scheduler.rate:10000}")
    public void sendData() {

        List<MasterData> data = new ArrayList<>();

        if (setterEnabled) {
            data.add(tg1008B510Data);
        }
        if (getterEnabled) {
            data.addAll(masterSlaveDataList);
        }
        ebusReaderWriter.replaceMasterDataQueue(data);
        log.debug("eBUS MasterData queue updated, {} telegrams", data.size());
    }

    /**
     * Setting MasterSlaveData.
     * @param masterSlaveData (Tg1008B510Data) telegram.
     */
    public void sendDataImmidiately(Tg1008B510Data masterSlaveData) {
        if (masterSlaveData == null) {
            return;
        }
        this.tg1008B510Data = masterSlaveData;
    }
}