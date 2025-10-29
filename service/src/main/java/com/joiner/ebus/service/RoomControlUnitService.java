
package com.joiner.ebus.service;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.DataCollector;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.converter.MasterDataToRoomControlUnitDtoConverter;
import com.joiner.ebus.service.converter.RoomControlUnitDtoToTg1008B510DataConverter;

import lombok.Getter;

@Service
public class RoomControlUnitService {

    @Autowired
    private MasterDataToRoomControlUnitDtoConverter converter1;
    
    @Autowired
    private RoomControlUnitDtoToTg1008B510DataConverter converter2;

    @Autowired
    private DataCollector dataCollector;

    @Getter
    private RoomControlUnitDto roomControlUnitDto;
    
    public void setRoomControlUnit(RoomControlUnitDto roomControlUnitDtoStub) {
        roomControlUnitDto = converter1.convert(dataCollector.getMasterSlaveData());
        copyNonNullProperties(roomControlUnitDtoStub, roomControlUnitDto);
        dataCollector.sendDataImmidiately(converter2.convert(roomControlUnitDto));
    }

    /**
     * Copies properties from the source object to the target object, ignoring null values in the source.
     * This is useful for partial updates where only non-null fields should be updated.
     *
     * @param source The source object from which properties are copied.
     * @param target The target object to which properties are copied.
     */
    private void copyNonNullProperties(Object source, Object target) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        String[] nullPropertyNames = Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }

}
