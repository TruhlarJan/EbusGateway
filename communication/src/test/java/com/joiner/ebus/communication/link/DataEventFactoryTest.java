package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.joiner.ebus.communication.link.DataEventFactory.Address03h15hB5h13hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Address03h64hB5h12hDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.AddressUnknownDataReadyEvent;
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.AddressUnknownData;

@SpringBootTest
class DataEventFactoryTest {

    @Autowired
    private DataEventFactory dataEventFactory;

    @Test
    void testSave() {
        Address03h15hB5h13hDataReadyEvent dataReadyEvent = (Address03h15hB5h13hDataReadyEvent) dataEventFactory.getDataReadyEvent(this, new byte[] {0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06});
        assertEquals(Address03h15hB5h13hData.KEY, dataReadyEvent.getData().getKey());

        Address03h64hB5h12hDataReadyEvent dataReadyEvent2 = (Address03h64hB5h12hDataReadyEvent) dataEventFactory.getDataReadyEvent(this, new byte[] {0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02});
        assertEquals(Address03h64hB5h12hData.KEY, dataReadyEvent2.getData().getKey());

        AddressUnknownDataReadyEvent dataReadyEvent3 = (AddressUnknownDataReadyEvent) dataEventFactory.getDataReadyEvent(this, new byte[6]);
        assertEquals(AddressUnknownData.KEY, dataReadyEvent3.getData().getKey());

    }

}
