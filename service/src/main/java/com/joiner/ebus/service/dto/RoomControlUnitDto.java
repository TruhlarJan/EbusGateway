package com.joiner.ebus.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomControlUnitDto {

    Double leadWaterTargetTemperature;

    Double serviceWaterTargetTemperature;

    boolean isLeadWaterHeatingBlocked;

    boolean isServiceWaterHeatingBlocked;

}
