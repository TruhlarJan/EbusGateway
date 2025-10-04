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
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h10hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCache {

    @Autowired
    private ByteUtils byteUtils;

    @Getter
    private List<MasterSlaveData> unknownList = new ArrayList<>();

    @Getter
    private Map<Long, MasterSlaveData> masterSlaveDataMap = new HashMap<>();

    @Async
    @EventListener
    public void handleFrame(MasterSlaveDataReadyEvent event) {
        MasterSlaveData masterSlaveData = event.getMasterSlaveData();
        if (ByteUtils.isAllZero(masterSlaveData.getSlaveData())) {
            return;
        }
        long key = masterSlaveData.getKey();
        if (key == Address10h08hB5h10hData.KEY) {
            masterSlaveDataMap.put(key, masterSlaveData);
        } else if (key == Address10h08hB5h11h01h00hData.KEY) {
            masterSlaveDataMap.put(key, masterSlaveData);
        } else if (key == Address10h08hB5h11h01h01hData.KEY) {
            masterSlaveDataMap.put(key, masterSlaveData);
        } else if (key == Address10h08hB5h11h01h02hData.KEY) {
            masterSlaveDataMap.put(key, masterSlaveData);
        } else if (key == Address03h15hB5h13hData.KEY) {
            masterSlaveDataMap.put(key, masterSlaveData);
        } else if (key == Address03h64hB5h12hData.KEY) {
            masterSlaveDataMap.put(key, masterSlaveData);
        } else {
            unknownList.add(masterSlaveData);
        }
        log.debug("Done masterSlave data. Key: {} , bytes: {} {}", key, byteUtils.bytesToHex(masterSlaveData.getMasterData()), byteUtils.bytesToHex(masterSlaveData.getSlaveData()));
    }
    
}
