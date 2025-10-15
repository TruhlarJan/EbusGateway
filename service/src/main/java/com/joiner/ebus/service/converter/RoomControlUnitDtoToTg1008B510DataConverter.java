package com.joiner.ebus.service.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.model.RoomControlUnitDto;

@Component
public class RoomControlUnitDtoToTg1008B510DataConverter implements Converter<RoomControlUnitDto, Tg1008B510Data> {

    @Override
    public Tg1008B510Data convert(RoomControlUnitDto source) {
        int m8 = (byte) Math.round(source.getLeadWaterTargetTemperature() * 2);
        int m9 = (byte) Math.round(source.getServiceWaterTargetTemperature() * 2);
        int m12 = source.getLeadWaterHeatingBlocked() + (source.getServiceWaterHeatingBlocked() << 2);
        return new Tg1008B510Data(m8, m9, m12);
    }

}
