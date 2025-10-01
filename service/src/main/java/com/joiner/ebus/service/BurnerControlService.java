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
import com.joiner.ebus.service.dto.BurnerControlUnitsDto;
import com.joiner.ebus.service.converter.source.Bit.*;
import com.joiner.ebus.service.converter.source.Hex.*;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock00Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock01Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock02Dto;

@Service
public class BurnerControlService {

    private Map<Long, MasterSlaveData> masterSlaveDataMap;
    
    @Autowired
    private ConversionService conversionService;

    public BurnerControlService(DataCollector dataCollector) {
        masterSlaveDataMap = dataCollector.getMasterSlaveDataMap();
    }
    
    /**
     * Operational Data of Burner Control Unit to Room Control Unit (B5h 11h Block 1)
     */
    public BurnerControlUnitsDto getBurnerControlUnits() {
        BurnerControlUnitsDto burnerControlUnit = new BurnerControlUnitsDto();
        burnerControlUnit.setBlock00(getBurnerControlUnitBlock00Dto());
        burnerControlUnit.setBlock01(getBurnerControlUnitBlock01Dto());
        burnerControlUnit.setBlock02(getBurnerControlUnitBlock02Dto());
        return burnerControlUnit;
    }

    /**
     * @param masterSlaveDataMap
     * @return
     */
    public BurnerControlUnitBlock00Dto getBurnerControlUnitBlock00Dto() {
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Address10h08hB5h11h01h00hData.KEY);
        
        BurnerControlUnitBlock00Dto dto = new BurnerControlUnitBlock00Dto();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        return dto;
    }
    
    /**
     * @param masterSlaveDataMap
     * @return
     */
    public BurnerControlUnitBlock01Dto getBurnerControlUnitBlock01Dto() {
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
    public BurnerControlUnitBlock02Dto getBurnerControlUnitBlock02Dto() {
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Address10h08hB5h11h01h02hData.KEY);
        byte[] slaveData = masterSlaveData.getSlaveData();
        
        BurnerControlUnitBlock02Dto dto = new BurnerControlUnitBlock02Dto();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        dto.setServiceWaterTargetTemperature(conversionService.convert(S7.of(slaveData), Double.class));
        return dto;
    }
}
