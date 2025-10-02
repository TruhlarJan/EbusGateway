package com.joiner.ebus.communication;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.EbusMasterSlaveLink;
import com.joiner.ebus.communication.link.EbusSlaveMasterLink;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCollector {

    @Value("${collector.poller.enabled:true}")
    private boolean pollerEnabled;

    @Autowired
    private EbusMasterSlaveLink ebusMasterSlaveLink;

    @Autowired
    private EbusSlaveMasterLink ebusSlaveMasterLink;

    @Autowired
    private ByteUtils byteUtils;
    
    private final ReentrantLock ebusLock = new ReentrantLock();

    @PostConstruct
    public void init() {
        ebusMasterSlaveLink.setLock(ebusLock);
        ebusSlaveMasterLink.setLock(ebusLock);
    }

    @Scheduled(fixedRateString = "${collector.scheduler.rate:10000}")
    public void sendData() {
        if (!pollerEnabled) {
            return;
        }

        List<MasterSlaveData> list = List.of(
                new Address10h08hB5h11h01h00hData(),
                new Address10h08hB5h11h01h01hData(),
                new Address10h08hB5h11h01h02hData());

        for (MasterSlaveData masterSlaveData : list) {
            try {
                ebusMasterSlaveLink.sendFrame(masterSlaveData);
                log.debug("Slave response: {}", byteUtils.bytesToHex(masterSlaveData.getSlaveData()));
            } catch (Exception e) {
                log.error("Ebus MasterToSlave communication failed.", e);
            }
        }
    } 

    public byte[] sendDataImmidiately(MasterSlaveData masterSlaveData) {
        try {
            ebusMasterSlaveLink.sendFrame(masterSlaveData);
            return masterSlaveData.getSlaveData();
        } catch (Exception e) {
            log.error("Ebus MasterToSlave communication failed.", e);
            throw new RuntimeException(e);
        }
    }

}
