package com.joiner.ebus.communication;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.link.EbusMasterSlaveLink;
import com.joiner.ebus.communication.link.EbusSlaveMasterLink;
import com.joiner.ebus.communication.link.FrameParsedEvent;
import com.joiner.ebus.communication.protherm.Address10h08hB5h10hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.SlaveData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
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

    private final ReentrantLock ebusLock = new ReentrantLock();

    @Getter
    private Map<Long, MasterSlaveData> masterSlaveDataMap = new HashMap<>();

    @Getter
    private Map<Long, SlaveData> masterDataMap = new HashMap<>();

    @PostConstruct
    public void init() {
        ebusMasterSlaveLink.setLock(ebusLock);
        ebusSlaveMasterLink.setLock(ebusLock);
        // data
        masterSlaveDataMap.put(Address10h08hB5h10hData.KEY, new Address10h08hB5h10hData());
        masterSlaveDataMap.put(Address10h08hB5h11h01h00hData.KEY, new Address10h08hB5h11h01h00hData());
        masterSlaveDataMap.put(Address10h08hB5h11h01h01hData.KEY, new Address10h08hB5h11h01h01hData());
        masterSlaveDataMap.put(Address10h08hB5h11h01h02hData.KEY, new Address10h08hB5h11h01h02hData());
    }

    @Scheduled(fixedRateString = "${collector.scheduler.rate:10000}")
    public void sendData() {
        if (!pollerEnabled) {
            return;
        }
        masterSlaveDataMap.values().forEach(masterSlaveDataValues -> {
            try {
                byte[] masterEcho = ebusMasterSlaveLink.sendFrame(masterSlaveDataValues);
                log.debug("Master echo:    {}", bytesToHex(masterEcho));
                log.debug("Slave response: {}", bytesToHex(masterSlaveDataValues.getSlaveData()));
            } catch (Exception e) {
                log.error("Ebus MasterToSlave communication failed.", e);
            }
        });
    } 

    @Async
    @EventListener
    public void handleFrame(FrameParsedEvent event) {
        long key = event.getKey();
        SlaveData slaveData = event.getSlaveData();
        masterDataMap.put(key, slaveData);
        log.debug("Intercepted slave data. Key: {} , bytes: {} {}", key, bytesToHex(slaveData.getAddress()), bytesToHex(slaveData.getData()));
    }

    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

}
