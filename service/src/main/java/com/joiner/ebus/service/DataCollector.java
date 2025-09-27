package com.joiner.ebus.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.EbusMasterSlaveLink;
import com.joiner.ebus.communication.EbusSlaveMasterLink;
import com.joiner.ebus.communication.FrameParser;
import com.joiner.ebus.communication.FrameParsedEvent;
import com.joiner.ebus.communication.MasterData;
import com.joiner.ebus.communication.MasterSlaveData;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCollector {

    @Value("${collector.poller.enabled:true}")
    private boolean pollerEnabled;
    
    @Autowired
    private EbusMasterSlaveLink dataSender;

    @Autowired
    private EbusSlaveMasterLink dataListener;

    private final ReentrantLock ebusLock = new ReentrantLock();

    private Map<Long, MasterData> map = new HashMap<>();
    
    @PostConstruct
    public void init() {
        dataSender.setLock(ebusLock);
        dataListener.setLock(ebusLock);
    }

    @Scheduled(fixedRateString = "${collector.scheduler.rate:10000}")
    public void sendData() {
        if (!pollerEnabled) {
            return;
        }

        RoomController roomController = new RoomController();
        log.info("Client sending RoomController data: 30, 45.0, false, true");
        MasterSlaveData masterSlaveData = roomController.getOperationalData(30, 45.0, false, true);

        try {
            byte[] masterEcho = dataSender.sendFrame(masterSlaveData);
            log.debug("Master echo:    {}", bytesToHex(masterEcho));
            log.debug("Slave response: {}", bytesToHex(masterSlaveData.getSlaveData()));
            log.debug("Client adapted data -> Acknowledge: {}", roomController.getAcknowledge());
            
            FrameParser frameParser = dataListener.getFrameParser();
            frameParser.getMap().forEach((k, v) -> log.info("Intercepted address: {}: {}", k, bytesToHex(frameParser.longToBytes(k))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    @Async
    @EventListener
    public void handleFrame(FrameParsedEvent event) {
        MasterData masterData = event.getMasterData();
        map.put(event.getKey(), masterData);
        log.info("Intercepted data: {} {}", bytesToHex(masterData.getAddress()), bytesToHex(masterData.getData()));
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

}
