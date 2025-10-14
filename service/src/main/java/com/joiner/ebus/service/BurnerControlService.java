package com.joiner.ebus.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.service.cache.DataCache;
import com.joiner.ebus.service.converter.source.Bit.S9b0;
import com.joiner.ebus.service.converter.source.Bit.S9b2;
import com.joiner.ebus.service.converter.source.Hex.S3;
import com.joiner.ebus.service.converter.source.Hex.S4;
import com.joiner.ebus.service.converter.source.Hex.S7;
import com.joiner.ebus.service.converter.source.Hex.S8;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock00Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock01Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock02Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitsDto;

@Service
public class BurnerControlService {

    private Map<Long, MasterSlaveData> masterSlaveDataMap;
    
    @Autowired
    private ConversionService conversionService;

    public BurnerControlService(DataCache dataCache) {
        masterSlaveDataMap = dataCache.getMasterSlaveDataMap();
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
        BurnerControlUnitBlock00Dto dto = new BurnerControlUnitBlock00Dto();
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Tg1008B5110100Data.KEY);
        if (masterSlaveData == null) {
            return dto;
        }
        byte[] slaveData = masterSlaveData.getSlaveData();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        dto.setDate(masterSlaveData.getDate());
        return dto;
    }
    
    /**
     * @param masterSlaveDataMap
     * @return
     */
    public BurnerControlUnitBlock01Dto getBurnerControlUnitBlock01Dto() {
        BurnerControlUnitBlock01Dto dto = new BurnerControlUnitBlock01Dto();
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Tg1008B5110101Data.KEY);
        if (masterSlaveData == null) {
            return dto;
        }
        byte[] slaveData = masterSlaveData.getSlaveData();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        dto.setDate(masterSlaveData.getDate());
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
        BurnerControlUnitBlock02Dto dto = new BurnerControlUnitBlock02Dto();
        MasterSlaveData masterSlaveData = masterSlaveDataMap.get(Tg1008B5110102Data.KEY);
        if (masterSlaveData == null) {
            return dto;
        }
        byte[] slaveData = masterSlaveData.getSlaveData();
        dto.setData(conversionService.convert(masterSlaveData, String.class));
        dto.setDate(masterSlaveData.getDate());
        dto.setServiceWaterTargetTemperature(conversionService.convert(S7.of(slaveData), Double.class));
        return dto;
    }
}
