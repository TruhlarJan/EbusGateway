package com.joiner.ebus.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BurnerControlUnitBlock01Dto {
    
    String data;
    
    Double leadWaterTemperature;
    
    Double returnWaterTemperature;
    
    Double serviceWaterTemperature;
    
    Boolean heating;
    
    Boolean serviceWater;
}
