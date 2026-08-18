package com.joiner.ebus.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.link.EbusReaderWriter;
import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;

class DataCollectorTest {

    @Test
    void sendData_whenSetterAndGetterEnabled_replacesQueueWithSetterThenGetterTelegrams() {
        DataCollector collector = newDataCollector(true, true);
        EbusReaderWriter readerWriter = getReaderWriter(collector);

        Tg1008B510Data setterTelegram = new Tg1008B510Data();

        collector.sendDataImmidiately(setterTelegram);
        collector.sendData();

        ArgumentCaptor<List<MasterData>> captor = ArgumentCaptor.forClass(List.class);

        verify(readerWriter).replaceMasterDataQueue(captor.capture());

        List<MasterData> data = captor.getValue();

        assertEquals(4, data.size());
        assertSame(setterTelegram, data.get(0));
        assertInstanceOf(Tg1008B5110100Data.class, data.get(1));
        assertInstanceOf(Tg1008B5110101Data.class, data.get(2));
        assertInstanceOf(Tg1008B5110102Data.class, data.get(3));
    }

    @Test
    void sendData_whenOnlySetterEnabled_replacesQueueWithOnlySetterTelegram() {
        DataCollector collector = newDataCollector(true, false);
        EbusReaderWriter readerWriter = getReaderWriter(collector);

        Tg1008B510Data setterTelegram = new Tg1008B510Data();

        collector.sendDataImmidiately(setterTelegram);
        collector.sendData();

        ArgumentCaptor<List<MasterData>> captor = ArgumentCaptor.forClass(List.class);

        verify(readerWriter).replaceMasterDataQueue(captor.capture());

        List<MasterData> data = captor.getValue();

        assertEquals(1, data.size());
        assertSame(setterTelegram, data.get(0));
    }

    @Test
    void sendData_whenOnlyGetterEnabled_replacesQueueWithGetterTelegrams() {
        DataCollector collector = newDataCollector(false, true);
        EbusReaderWriter readerWriter = getReaderWriter(collector);

        collector.sendData();

        ArgumentCaptor<List<MasterData>> captor = ArgumentCaptor.forClass(List.class);

        verify(readerWriter).replaceMasterDataQueue(captor.capture());

        List<MasterData> data = captor.getValue();

        assertEquals(3, data.size());
        assertInstanceOf(Tg1008B5110100Data.class, data.get(0));
        assertInstanceOf(Tg1008B5110101Data.class, data.get(1));
        assertInstanceOf(Tg1008B5110102Data.class, data.get(2));
    }

    @Test
    void sendData_whenBothSetterAndGetterDisabled_replacesQueueWithEmptyList() {
        DataCollector collector = newDataCollector(false, false);
        EbusReaderWriter readerWriter = getReaderWriter(collector);

        collector.sendData();

        ArgumentCaptor<List<MasterData>> captor = ArgumentCaptor.forClass(List.class);

        verify(readerWriter).replaceMasterDataQueue(captor.capture());

        assertEquals(0, captor.getValue().size());
    }

    @Test
    void sendDataImmediately_updatesSetterTelegram() {
        DataCollector collector = newDataCollector(true, false);

        Tg1008B510Data setterTelegram = new Tg1008B510Data();

        collector.sendDataImmidiately(setterTelegram);

        assertSame(setterTelegram, collector.getTg1008B510Data());
    }

    private static DataCollector newDataCollector(boolean setterEnabled, boolean getterEnabled) {

        DataCollector collector = new DataCollector();

        EbusReaderWriter readerWriter = mock(EbusReaderWriter.class);

        ReflectionTestUtils.setField(collector, "ebusReaderWriter", readerWriter);

        ReflectionTestUtils.setField(collector, "setterEnabled", setterEnabled);

        ReflectionTestUtils.setField(collector, "getterEnabled", getterEnabled);

        return collector;
    }

    private static EbusReaderWriter getReaderWriter(DataCollector collector) {

        return (EbusReaderWriter) ReflectionTestUtils.getField(collector, "ebusReaderWriter");
    }
}