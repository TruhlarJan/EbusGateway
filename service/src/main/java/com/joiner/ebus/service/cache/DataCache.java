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
import com.joiner.ebus.communication.link.MasterSlaveDataReadyEvent;
import com.joiner.ebus.communication.link.SlaveDataReadyEvent;
import com.joiner.ebus.communication.protherm.AddressUnknownData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.communication.protherm.SlaveData;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCache {

    @Autowired
    private ByteUtils byteUtils;

    @Getter
    private List<SlaveData> unknownList = new ArrayList<>();
    
    @Getter
    private Map<Long, SlaveData> slaveDataMap = new HashMap<>();

    @Getter
    private Map<Long, MasterSlaveData> masterSlaveDataMap = new HashMap<>();

    @Async
    @EventListener
    public void handleFrame(SlaveDataReadyEvent event) {
        SlaveData slaveData = event.getSlaveData();
        long key = slaveData.getKey();
        if (key == AddressUnknownData.KEY) {
            unknownList.add(slaveData);
        } else {
            slaveDataMap.put(key, slaveData);
        }
        log.debug("Intercepted slave data. Key: {} , bytes: {} {}", key, byteUtils.bytesToHex(slaveData.getAddress()), byteUtils.bytesToHex(slaveData.getData()));
    }

    @Async
    @EventListener
    public void handleFrame(MasterSlaveDataReadyEvent event) {
        MasterSlaveData masterSlaveData = event.getMasterSlaveData();
        long key = masterSlaveData.getKey();
        masterSlaveDataMap.put(key, masterSlaveData);
        log.debug("Done masterSlave data. Key: {} , bytes: {} {} {}", key, byteUtils.bytesToHex(masterSlaveData.getMasterStartData()), byteUtils.bytesToHex(masterSlaveData.getSlaveData()), byteUtils.bytesToHex(masterSlaveData.getMasterFinalData()));
    }
    
}
