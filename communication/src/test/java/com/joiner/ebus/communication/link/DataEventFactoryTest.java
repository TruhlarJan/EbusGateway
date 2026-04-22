package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.joiner.ebus.communication.link.DataEventFactory.MasterDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.MasterSlaveDataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg0315B513DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg0364B512DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B510DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110100DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110101DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B5110102DataReadyEvent;
import com.joiner.ebus.communication.link.DataEventFactory.TgUnknownDataReadyEvent;
import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.communication.protherm.Tg0315B513Data;
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
import com.joiner.ebus.communication.protherm.TgUnknownData;

class DataEventFactoryTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    @Test
    void getDataReadyEvent_returnsSpecializedEvent_forKnownMasterSlaveData() {
        Tg1008B510Data tg1008B510Data = new Tg1008B510Data();
        Tg1008B5110100Data tg1008B5110100Data = new Tg1008B5110100Data();
        Tg1008B5110101Data tg1008B5110101Data = new Tg1008B5110101Data();
        Tg1008B5110102Data tg1008B5110102Data = new Tg1008B5110102Data();

        Tg1008B510DataReadyEvent event1 = assertInstanceOf(Tg1008B510DataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(tg1008B510Data));
        assertSame(tg1008B510Data, event1.getData());

        Tg1008B5110100DataReadyEvent event2 = assertInstanceOf(Tg1008B5110100DataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(tg1008B5110100Data));
        assertSame(tg1008B5110100Data, event2.getData());

        Tg1008B5110101DataReadyEvent event3 = assertInstanceOf(Tg1008B5110101DataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(tg1008B5110101Data));
        assertSame(tg1008B5110101Data, event3.getData());

        Tg1008B5110102DataReadyEvent event4 = assertInstanceOf(Tg1008B5110102DataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(tg1008B5110102Data));
        assertSame(tg1008B5110102Data, event4.getData());
    }

    @Test
    void getDataReadyEvent_returnsFallbackEvent_forUnknownMasterSlaveSubtype() {
        MasterSlaveData masterSlaveData = new TestMasterSlaveData();

        MasterSlaveDataReadyEvent event = assertInstanceOf(MasterSlaveDataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(masterSlaveData));

        assertSame(masterSlaveData, event.getData());
    }

    @Test
    void getDataReadyEvent_returnsSpecializedEvent_forKnownMasterData() {
        Tg0315B513Data tg0315B513Data = new Tg0315B513Data(new byte[] { 0x03, 0x15, (byte) 0xB5, 0x13, 0x03, 0x06 });
        Tg0364B512Data tg0364B512Data = new Tg0364B512Data(new byte[] { 0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02 });
        TgUnknownData tgUnknownData = new TgUnknownData(new byte[6]);

        Tg0315B513DataReadyEvent event1 = assertInstanceOf(Tg0315B513DataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(tg0315B513Data));
        assertSame(tg0315B513Data, event1.getData());

        Tg0364B512DataReadyEvent event2 = assertInstanceOf(Tg0364B512DataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(tg0364B512Data));
        assertSame(tg0364B512Data, event2.getData());

        TgUnknownDataReadyEvent event3 = assertInstanceOf(TgUnknownDataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(tgUnknownData));
        assertSame(tgUnknownData, event3.getData());
    }

    @Test
    void getDataReadyEvent_returnsFallbackEvent_forUnknownMasterDataSubtype() {
        MasterData masterData = new TestMasterData();

        MasterDataReadyEvent event = assertInstanceOf(MasterDataReadyEvent.class,
                dataEventFactory.getDataReadyEvent(masterData));

        assertSame(masterData, event.getData());
    }

    private static class TestMasterData implements MasterData {

        private byte[] masterData = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };
        private Date date = new Date();

        @Override
        public long getKey() {
            return 42L;
        }

        @Override
        public byte[] getMasterData() {
            return masterData;
        }

        @Override
        public void setMasterData(byte[] masterData) {
            this.masterData = masterData;
        }

        @Override
        public Date getDate() {
            return date;
        }

        @Override
        public void setDate(Date date) {
            this.date = date;
        }
    }

    private static final class TestMasterSlaveData extends TestMasterData implements MasterSlaveData {

        private byte[] slaveData = new byte[] { ACK, 0x00, 0x00, 0x00 };

        @Override
        public byte[] getSlaveData() {
            return slaveData;
        }

        @Override
        public void setSlaveData(byte[] slaveData) {
            this.slaveData = slaveData;
        }
    }
}
