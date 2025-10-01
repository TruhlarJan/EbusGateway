package com.joiner.ebus.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.DataCollector;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.service.converter.Bit.*;
import com.joiner.ebus.service.converter.Hex.*;
import com.joiner.ebus.service.dto.BurnerControlUnit;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock00Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock01Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock02Dto;

@Service
public class BurnerControlService {

    @Autowired
    private DataCollector dataCollector;
    
    @Autowired
    private ConversionService conversionService;

    /**
     * Operational Data of Burner Control Unit to Room Control Unit (B5h 11h Block 1)
     */
    public BurnerControlUnit getBurnerControlUnit() {
        Map<Long, MasterSlaveData> masterSlaveDataMap = dataCollector.getMasterSlaveDataMap();
        BurnerControlUnit burnerControlUnit = new BurnerControlUnit();
        burnerControlUnit.setBlock00(getBurnerControlUnitBlock00Dto(masterSlaveDataMap));
        burnerControlUnit.setBlock01(getBurnerControlUnitBlock01Dto(masterSlaveDataMap));
        burnerControlUnit.setBlock02(getBurnerControlUnitBlock02Dto(masterSlaveDataMap));
        return burnerControlUnit;
    }

    /**
     * @param masterSlaveDataMap
     * @return
     */
    private BurnerControlUnitBlock00Dto getBurnerControlUnitBlock00Dto(Map<Long, MasterSlaveData> masterSlaveDataMap) {
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Address10h08hB5h11h01h00hData.KEY);
        
        BurnerControlUnitBlock00Dto dto = new BurnerControlUnitBlock00Dto();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        return dto;
    }
    
    /**
     * @param masterSlaveDataMap
     * @return
     */
    private BurnerControlUnitBlock01Dto getBurnerControlUnitBlock01Dto(Map<Long, MasterSlaveData> masterSlaveDataMap) {
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Address10h08hB5h11h01h01hData.KEY);
        byte[] slaveData = masterSlaveData.getSlaveData();
        
        BurnerControlUnitBlock01Dto dto = new BurnerControlUnitBlock01Dto();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        dto.setLeadWaterTemperature(conversionService.convert(S3.of(slaveData), Double.class));
        dto.setReturnWaterTemperature(conversionService.convert(S4.of(slaveData), Double.class));
        dto.setServiceWaterTemperature(conversionService.convert(S8.of(slaveData), Double.class));
        dto.setHeating(conversionService.convert(S9b0.of(slaveData) , Boolean.class));
        dto.setServiceWater(conversionService.convert(S9b2.of(slaveData), Boolean.class));
        return dto;
    }

    /**
     * @param masterSlaveDataMap
     * @return
     */
    private BurnerControlUnitBlock02Dto getBurnerControlUnitBlock02Dto(Map<Long, MasterSlaveData> masterSlaveDataMap) {
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Address10h08hB5h11h01h02hData.KEY);
        byte[] slaveData = masterSlaveData.getSlaveData();
        
        BurnerControlUnitBlock02Dto dto = new BurnerControlUnitBlock02Dto();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        dto.setServiceWaterTargetTemperature(conversionService.convert(S7.of(slaveData), Double.class));
        return dto;
    }
}
