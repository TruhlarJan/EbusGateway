package com.joiner.ebus.service.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.link.SlaveDataReadyEvent;
import com.joiner.ebus.communication.protherm.SlaveData;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCache {

    @Autowired
    private ByteUtils byteUtils;

    @Getter
    private Map<Long, SlaveData> masterDataMap = new HashMap<>();

    @Async
    @EventListener
    public void handleFrame(SlaveDataReadyEvent event) {
        SlaveData slaveData = event.getSlaveData();
        long key = slaveData.getKey();
        masterDataMap.put(key, slaveData);
        log.debug("Intercepted slave data. Key: {} , bytes: {} {}", key, byteUtils.bytesToHex(slaveData.getAddress()), byteUtils.bytesToHex(slaveData.getData()));
    }

    
}
