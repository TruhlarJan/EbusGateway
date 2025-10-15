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
        Tg0315B513DataReadyEvent dataReadyEvent = (Tg0315B513DataReadyEvent) dataEventFactory.getDataReadyEvent(new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06});
        assertEquals(Tg0315B513Data.KEY, dataReadyEvent.getData().getKey());

        Tg0364B512DataReadyEvent dataReadyEvent2 = (Tg0364B512DataReadyEvent) dataEventFactory.getDataReadyEvent(new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02});
        assertEquals(Tg0364B512Data.KEY, dataReadyEvent2.getData().getKey());

        TgUnknownDataReadyEvent dataReadyEvent3 = (TgUnknownDataReadyEvent) dataEventFactory.getDataReadyEvent(new byte[6]);
        assertEquals(TgUnknownData.KEY, dataReadyEvent3.getData().getKey());

    }

}
