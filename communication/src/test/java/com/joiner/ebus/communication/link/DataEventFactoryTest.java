package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.communication.link.DataEventFactory.Tg0315B513DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg0364B512DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.TgUnknownDataReadyEvent;
import com.joiner.ebus.communication.protherm.Tg0315B513Data;
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.communication.protherm.TgUnknownData;

@SpringBootTest
class DataEventFactoryTest {

    @Autowired
    private DataEventFactory dataEventFactory;

    @Test
    void testSave() {
    	Tg0315B513DataReadyEvent tg0315b513DataReadyEvent = (Tg0315B513DataReadyEvent) dataEventFactory.getDataReadyEvent(new Tg0315B513Data(new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06}));
        assertEquals(Tg0315B513Data.KEY, tg0315b513DataReadyEvent.getData().getKey());

        Tg0364B512DataReadyEvent tg0364b512DataReadyEvent = (Tg0364B512DataReadyEvent) dataEventFactory.getDataReadyEvent(new Tg0364B512Data(new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02}));
        assertEquals(Tg0364B512Data.KEY, tg0364b512DataReadyEvent.getData().getKey());

        TgUnknownDataReadyEvent tgUnknownDataReadyEvent = (TgUnknownDataReadyEvent) dataEventFactory.getDataReadyEvent(new TgUnknownData((new byte[6])));
        assertEquals(TgUnknownData.KEY, tgUnknownDataReadyEvent.getData().getKey());
    }

}
