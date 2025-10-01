package com.joiner.ebus.service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BurnerControlUnitsDto {

    private BurnerControlUnitBlock00Dto block00;

    private BurnerControlUnitBlock01Dto block01;
    
    private BurnerControlUnitBlock02Dto block02;
}
