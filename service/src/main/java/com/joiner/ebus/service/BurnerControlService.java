package com.joiner.ebus.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.DataCollector;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock01Dto;

@Service
public class BurnerControlService {

    @Autowired
    private DataCollector dataCollector;
    
    @Autowired
    private ConversionService conversionService;

    /**
     * Operational Data of Burner Control Unit to Room Control Unit (B5h 11h Block 1)
     */
    public BurnerControlUnitBlock01Dto getBurnerControlUnitBlock01() {
        Map<Long, MasterSlaveData> masterSlaveDataMap = dataCollector.getMasterSlaveDataMap();
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Address10h08hB5h11h01h01hData.KEY);
        
        BurnerControlUnitBlock01Dto dto = new BurnerControlUnitBlock01Dto();
        dto.setMasterStartData(dataCollector.bytesToHex(masterSlaveData.getMasterStartData()));
        dto.setSlaveData(dataCollector.bytesToHex(masterSlaveData.getSlaveData()));
        dto.setMasterFinalData(dataCollector.bytesToHex(masterSlaveData.getMasterFinalData()));

        byte[] slaveData = masterSlaveData.getSlaveData();
        dto.setLeadWaterTemperature(conversionService.convert(slaveData[2], Double.class));
        dto.setReturnWaterTemperature(conversionService.convert(slaveData[3], Double.class));
        dto.setServiceWaterTemperature(conversionService.convert(slaveData[7], Double.class));
        dto.setHeating(conversionService.convert(slaveData[8] & 1 , Boolean.class));
        dto.setServiceWater(conversionService.convert((slaveData[8] >> 2) & 1 , Boolean.class));
        return dto;
    }
    
}
