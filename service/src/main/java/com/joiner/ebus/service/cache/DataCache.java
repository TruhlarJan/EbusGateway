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
import com.joiner.ebus.communication.link.DataEventFactory.Address03h15hB5h13hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Address03h64hB5h12hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Address10h08hB5h10hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Address10h08hB5h11h01h00hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Address10h08hB5h11h01h01hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Address10h08hB5h11h01h02hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.AddressUnknownDataReadyEvent;
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
    public void handleFrame(Address10h08hB5h10hDataReadyEvent event) {
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
    public void handleFrame(Address10h08hB5h11h01h00hDataReadyEvent event) {
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
    public void handleFrame(Address10h08hB5h11h01h01hDataReadyEvent event) {
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
    public void handleFrame(Address10h08hB5h11h01h02hDataReadyEvent event) {
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
    public void handleFrame(Address03h15hB5h13hDataReadyEvent event) {
        MasterData masterData = event.getData();
        long key = masterData.getKey();
        masterDataMap.put(key, masterData);
        log.debug("Done masterSlave data. Key: {} , bytes: {}", key, byteUtils.bytesToHex(masterData.getMasterData()));
    }

    @Async
    @EventListener
    public void handleFrame(Address03h64hB5h12hDataReadyEvent event) {
        MasterData masterData = event.getData();
        long key = masterData.getKey();
        masterDataMap.put(key, masterData);
        log.debug("Done masterSlave data. Key: {} , bytes: {}", key, byteUtils.bytesToHex(masterData.getMasterData()));
    }

    @Async
    @EventListener
    public void handleFrame(AddressUnknownDataReadyEvent event) {
        MasterData masterData = event.getData();
        long key = masterData.getKey();
        unknownList.add(masterData);
        log.debug("Done masterSlave data. Key: {} , bytes: {}", key, byteUtils.bytesToHex(masterData.getMasterData()));
    }

    
}
