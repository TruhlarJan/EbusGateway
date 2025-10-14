package com.joiner.ebus.service.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.link.DataEventFactory.Tg0315B513DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg0364B512DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B510DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110100DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110101DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110102DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.TgUnknownDataReadyEvent;
import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCache {

    @Autowired
    private ByteUtils byteUtils;

    @Getter
    private List<MasterData> unknownList = new ArrayList<>();

    @Getter
    private Map<Long, MasterSlaveData> masterSlaveDataMap = new HashMap<>();

    @Getter
    private Map<Long, MasterData> masterDataMap = new HashMap<>();
    
    
    @Async
    @EventListener
    public void handleFrame(Tg1008B510DataReadyEvent event) {
        MasterSlaveData masterSlaveData = event.getData();
        if (ByteUtils.isAllZero(masterSlaveData.getSlaveData())) {
            return;
        }
        long key = masterSlaveData.getKey();
        masterSlaveDataMap.put(key, masterSlaveData);
        log.debug("Done masterSlave data. Key: {} , bytes: {} {}", key, byteUtils.bytesToHex(masterSlaveData.getMasterData()), byteUtils.bytesToHex(masterSlaveData.getSlaveData()));
    }

    @Async
    @EventListener
    public void handleFrame(Tg1008B5110100DataReadyEvent event) {
        MasterSlaveData masterSlaveData = event.getData();
        if (ByteUtils.isAllZero(masterSlaveData.getSlaveData())) {
            return;
        }
        long key = masterSlaveData.getKey();
        masterSlaveDataMap.put(key, masterSlaveData);
        log.debug("Done masterSlave data. Key: {} , bytes: {} {}", key, byteUtils.bytesToHex(masterSlaveData.getMasterData()), byteUtils.bytesToHex(masterSlaveData.getSlaveData()));
    }

    @Async
    @EventListener
    public void handleFrame(Tg1008B5110101DataReadyEvent event) {
        MasterSlaveData masterSlaveData = event.getData();
        if (ByteUtils.isAllZero(masterSlaveData.getSlaveData())) {
            return;
        }
        long key = masterSlaveData.getKey();
        masterSlaveDataMap.put(key, masterSlaveData);
        log.debug("Done masterSlave data. Key: {} , bytes: {} {}", key, byteUtils.bytesToHex(masterSlaveData.getMasterData()), byteUtils.bytesToHex(masterSlaveData.getSlaveData()));
    }

    @Async
    @EventListener
    public void handleFrame(Tg1008B5110102DataReadyEvent event) {
        MasterSlaveData masterSlaveData = event.getData();
        if (ByteUtils.isAllZero(masterSlaveData.getSlaveData())) {
            return;
        }
        long key = masterSlaveData.getKey();
        masterSlaveDataMap.put(key, masterSlaveData);
        log.debug("Done masterSlave data. Key: {} , bytes: {} {}", key, byteUtils.bytesToHex(masterSlaveData.getMasterData()), byteUtils.bytesToHex(masterSlaveData.getSlaveData()));
    }

    @Async
    @EventListener
    public void handleFrame(Tg0315B513DataReadyEvent event) {
        MasterData masterData = event.getData();
        long key = masterData.getKey();
        masterDataMap.put(key, masterData);
        log.debug("Done masterSlave data. Key: {} , bytes: {}", key, byteUtils.bytesToHex(masterData.getMasterData()));
    }

    @Async
    @EventListener
    public void handleFrame(Tg0364B512DataReadyEvent event) {
        MasterData masterData = event.getData();
        long key = masterData.getKey();
        masterDataMap.put(key, masterData);
        log.debug("Done masterSlave data. Key: {} , bytes: {}", key, byteUtils.bytesToHex(masterData.getMasterData()));
    }

    @Async
    @EventListener
    public void handleFrame(TgUnknownDataReadyEvent event) {
        MasterData masterData = event.getData();
        long key = masterData.getKey();
        unknownList.add(masterData);
        log.debug("Done masterSlave data. Key: {} , bytes: {}", key, byteUtils.bytesToHex(masterData.getMasterData()));
    }

    
}
