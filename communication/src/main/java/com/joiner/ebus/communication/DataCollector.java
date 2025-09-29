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
import com.joiner.ebus.communication.protherm.MasterData;
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

    private final ReentrantLock ebusLock = new ReentrantLock();

    private Map<Long, MasterSlaveData> masterSlaveDataMap = new HashMap<>();

    private Map<Long, MasterData> masterDataMap = new HashMap<>();

    @PostConstruct
    public void init() {
        ebusMasterSlaveLink.setLock(ebusLock);
        ebusSlaveMasterLink.setLock(ebusLock);
        //test
        log.info("Client sending RoomController data: 30, 45.0, false, true");
        masterSlaveDataMap.put(1L, new Address10h08hB5h10hData(0x3C, 0x5A, 0x01));
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
        } );
        masterDataMap.forEach((k, v) -> log.info("Intercepted address: {}   {}   {}", k, bytesToHex(v.getAddress()), bytesToHex(v.getData())));
    } 

    @Async
    @EventListener
    public void handleFrame(FrameParsedEvent event) {
        MasterData masterData = event.getMasterData();
        masterDataMap.put(event.getKey(), masterData);
        log.debug("Intercepted data: {} {}", bytesToHex(masterData.getAddress()), bytesToHex(masterData.getData()));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

}
