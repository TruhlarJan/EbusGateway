package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.communication.link.DataEventFactory.Address03h15hB5h13hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Address03h64hB5h12hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.AddressUnknownDataReadyEvent;
import com.joiner.ebus.communication.protherm.Tg0315B513Data;
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.communication.protherm.TgUnknownData;

@SpringBootTest
class DataEventFactoryTest {

    @Autowired
    private DataEventFactory dataEventFactory;

    @Test
    void testSave() {
        Address03h15hB5h13hDataReadyEvent dataReadyEvent = (Address03h15hB5h13hDataReadyEvent) dataEventFactory.getDataReadyEvent(this, new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06});
        assertEquals(Tg0315B513Data.KEY, dataReadyEvent.getData().getKey());

        Address03h64hB5h12hDataReadyEvent dataReadyEvent2 = (Address03h64hB5h12hDataReadyEvent) dataEventFactory.getDataReadyEvent(this, new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02});
        assertEquals(Tg0364B512Data.KEY, dataReadyEvent2.getData().getKey());

        AddressUnknownDataReadyEvent dataReadyEvent3 = (AddressUnknownDataReadyEvent) dataEventFactory.getDataReadyEvent(this, new byte[6]);
        assertEquals(TgUnknownData.KEY, dataReadyEvent3.getData().getKey());

    }

}
