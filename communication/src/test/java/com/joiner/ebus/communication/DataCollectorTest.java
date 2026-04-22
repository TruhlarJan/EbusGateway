package com.joiner.ebus.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Queue;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.link.EbusReaderWriter;
import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;

class DataCollectorTest {

    @Test
    void sendData_whenSetterAndGetterEnabled_clearsQueueAndAddsSetterThenGetterTelegrams() {
        DataCollector collector = newDataCollector(true, true);
        EbusReaderWriter readerWriter = getReaderWriter(collector);
        Tg1008B510Data setterTelegram = new Tg1008B510Data();

        readerWriter.getMasterDataQueue().add(new Tg1008B510Data());
        collector.sendDataImmidiately(setterTelegram);

        collector.sendData();

        Queue<MasterData> queue = readerWriter.getMasterDataQueue();
        assertEquals(4, queue.size());
        assertSame(setterTelegram, queue.poll());
        assertInstanceOf(Tg1008B5110100Data.class, queue.poll());
        assertInstanceOf(Tg1008B5110101Data.class, queue.poll());
        assertInstanceOf(Tg1008B5110102Data.class, queue.poll());
    }

    @Test
    void sendData_whenOnlySetterEnabled_addsOnlySetterTelegram() {
        DataCollector collector = newDataCollector(true, false);
        EbusReaderWriter readerWriter = getReaderWriter(collector);
        Tg1008B510Data setterTelegram = new Tg1008B510Data();

        collector.sendDataImmidiately(setterTelegram);
        collector.sendData();

        Queue<MasterData> queue = readerWriter.getMasterDataQueue();
        assertEquals(1, queue.size());
        assertSame(setterTelegram, queue.peek());
    }

    @Test
    void sendData_whenOnlyGetterEnabled_addsOnlyGetterTelegrams() {
        DataCollector collector = newDataCollector(false, true);

        collector.sendData();

        Queue<MasterData> queue = getReaderWriter(collector).getMasterDataQueue();
        assertEquals(3, queue.size());
        assertInstanceOf(Tg1008B5110100Data.class, queue.poll());
        assertInstanceOf(Tg1008B5110101Data.class, queue.poll());
        assertInstanceOf(Tg1008B5110102Data.class, queue.poll());
    }

    @Test
    void sendData_whenBothSetterAndGetterDisabled_leavesQueueEmpty() {
        DataCollector collector = newDataCollector(false, false);
        EbusReaderWriter readerWriter = getReaderWriter(collector);

        readerWriter.getMasterDataQueue().add(new Tg1008B510Data());

        collector.sendData();

        assertTrue(readerWriter.getMasterDataQueue().isEmpty());
    }

    private static DataCollector newDataCollector(boolean setterEnabled, boolean getterEnabled) {
        DataCollector collector = new DataCollector();
        EbusReaderWriter readerWriter = new EbusReaderWriter();
        ReflectionTestUtils.setField(collector, "ebusReaderWriter", readerWriter);
        ReflectionTestUtils.setField(collector, "setterEnabled", setterEnabled);
        ReflectionTestUtils.setField(collector, "getterEnabled", getterEnabled);
        return collector;
    }

    private static EbusReaderWriter getReaderWriter(DataCollector collector) {
        return (EbusReaderWriter) ReflectionTestUtils.getField(collector, "ebusReaderWriter");
    }
}