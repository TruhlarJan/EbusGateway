package com.joiner.ebus.communication.link;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

class EbusReaderWriterTest {

    @Test
    void processByte_publishesParsedMasterSlaveEvent_andResetsBuffer() {
        EbusReaderWriter readerWriter = newReaderWriter();

        DataParser dataParser = mock(DataParser.class);
        DataEventFactory dataEventFactory = mock(DataEventFactory.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        TestMasterSlaveData parsedData = new TestMasterSlaveData();
        Object event = new Object();

        ReflectionTestUtils.setField(readerWriter, "dataParser", dataParser);
        ReflectionTestUtils.setField(readerWriter, "dataEventFactory", dataEventFactory);
        ReflectionTestUtils.setField(readerWriter, "publisher", publisher);

        when(dataParser.getMasterSlaveData(any(byte[].class))).thenReturn(parsedData);
        when(dataEventFactory.getDataReadyEvent(parsedData)).thenReturn(event);

        ReflectionTestUtils.invokeMethod(readerWriter, "processByte", 0x10);

        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);

        verify(dataParser).getMasterSlaveData(bytesCaptor.capture());

        assertArrayEquals(new byte[] { 0x10 }, bytesCaptor.getValue());

        verify(publisher).publishEvent(event);

        assertEquals(0, getBuffer(readerWriter).size());
    }

    @Test
    void processByte_whenFrameIsIncomplete_keepsBufferedBytes_andDoesNotPublish() {
        EbusReaderWriter readerWriter = newReaderWriter();

        DataParser dataParser = mock(DataParser.class);
        DataEventFactory dataEventFactory = mock(DataEventFactory.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        ReflectionTestUtils.setField(readerWriter, "dataParser", dataParser);
        ReflectionTestUtils.setField(readerWriter, "dataEventFactory", dataEventFactory);
        ReflectionTestUtils.setField(readerWriter, "publisher", publisher);

        when(dataParser.getMasterSlaveData(any(byte[].class))).thenReturn(null);

        ReflectionTestUtils.invokeMethod(readerWriter, "processByte", 0x22);

        assertArrayEquals(new byte[] { 0x22 }, getBuffer(readerWriter).toByteArray());

        verify(publisher, never()).publishEvent(any());
        verify(dataEventFactory, never()).getDataReadyEvent(any(MasterSlaveData.class));
    }

    @Test
    void processByte_whenBufferOverflows_resetsFrameBuffer() {
        EbusReaderWriter readerWriter = newReaderWriter();

        DataParser dataParser = mock(DataParser.class);

        ReflectionTestUtils.setField(readerWriter, "dataParser", dataParser);

        when(dataParser.getMasterSlaveData(any(byte[].class))).thenReturn(null);

        for (int i = 0; i < 65; i++) {
            ReflectionTestUtils.invokeMethod(readerWriter, "processByte", i);
        }

        assertEquals(0, getBuffer(readerWriter).size());
    }

    @Test
    void processMasterData_publishesParsedMasterEvent_andResetsBuffer() {
        EbusReaderWriter readerWriter = newReaderWriter();

        DataParser dataParser = mock(DataParser.class);
        DataEventFactory dataEventFactory = mock(DataEventFactory.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        TestMasterData parsedData = new TestMasterData();
        Object event = new Object();

        ReflectionTestUtils.setField(readerWriter, "dataParser", dataParser);
        ReflectionTestUtils.setField(readerWriter, "dataEventFactory", dataEventFactory);
        ReflectionTestUtils.setField(readerWriter, "publisher", publisher);

        getBuffer(readerWriter).writeBytes(new byte[] { 0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02 });

        when(dataParser.getMasterData(any(byte[].class))).thenReturn(parsedData);
        when(dataEventFactory.getDataReadyEvent(parsedData)).thenReturn(event);

        ReflectionTestUtils.invokeMethod(readerWriter, "processMasterData");

        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);

        verify(dataParser).getMasterData(bytesCaptor.capture());

        assertArrayEquals(new byte[] { 0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02 }, bytesCaptor.getValue());

        verify(publisher).publishEvent(event);

        assertEquals(0, getBuffer(readerWriter).size());
    }

    @Test
    void processMasterData_whenParserReturnsNull_resetsBuffer() {
        EbusReaderWriter readerWriter = newReaderWriter();

        DataParser dataParser = mock(DataParser.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        byte[] bufferedFrame = { 0x03, 0x64, (byte) 0xB5, 0x12, 0x02, 0x02 };

        ReflectionTestUtils.setField(readerWriter, "dataParser", dataParser);
        ReflectionTestUtils.setField(readerWriter, "publisher", publisher);

        getBuffer(readerWriter).writeBytes(bufferedFrame);

        when(dataParser.getMasterData(any(byte[].class))).thenReturn(null);

        ReflectionTestUtils.invokeMethod(readerWriter, "processMasterData");

        /*
         * Even an unknown Master telegram must be discarded. Otherwise it could be
         * concatenated with the next telegram.
         */
        assertEquals(0, getBuffer(readerWriter).size());

        verify(publisher, never()).publishEvent(any());
    }

    @Test
    void processMasterData_whenBufferIsEmpty_doesNothing() {
        EbusReaderWriter readerWriter = newReaderWriter();

        DataParser dataParser = mock(DataParser.class);
        ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

        ReflectionTestUtils.setField(readerWriter, "dataParser", dataParser);
        ReflectionTestUtils.setField(readerWriter, "publisher", publisher);

        ReflectionTestUtils.invokeMethod(readerWriter, "processMasterData");

        verify(dataParser, never()).getMasterData(any(byte[].class));
        verify(publisher, never()).publishEvent(any());

        assertEquals(0, getBuffer(readerWriter).size());
    }

    @Test
    void sendMasterData_writesQueuedMasterData_toOutputStream() throws Exception {
        EbusReaderWriter readerWriter = newReaderWriter();

        OutputStream out = mock(OutputStream.class);

        TestMasterData data = new TestMasterData();
        data.setMasterData(new byte[] { 0x10, 0x20, 0x30 });

        ReflectionTestUtils.setField(readerWriter, "out", out);

        replaceQueue(readerWriter, List.of(data));

        ReflectionTestUtils.invokeMethod(readerWriter, "sendMasterData");

        ArgumentCaptor<byte[]> bytesCaptor = ArgumentCaptor.forClass(byte[].class);

        verify(out).write(bytesCaptor.capture());
        verify(out).flush();

        assertArrayEquals(new byte[] { 0x10, 0x20, 0x30 }, bytesCaptor.getValue());

        assertEquals(0, getQueue(readerWriter).size());
    }

    @Test
    void replaceMasterDataQueue_replacesExistingQueue() {
        EbusReaderWriter readerWriter = newReaderWriter();

        TestMasterData oldData = new TestMasterData();
        TestMasterData newData1 = new TestMasterData();
        TestMasterData newData2 = new TestMasterData();

        replaceQueue(readerWriter, List.of(oldData));

        readerWriter.replaceMasterDataQueue(List.of(newData1, newData2));

        Queue<MasterData> queue = getQueue(readerWriter);

        assertEquals(2, queue.size());
        assertEquals(newData1, queue.poll());
        assertEquals(newData2, queue.poll());
    }

    @Test
    void replaceMasterDataQueue_whenDataIsNull_clearsQueue() {
        EbusReaderWriter readerWriter = newReaderWriter();

        replaceQueue(readerWriter, List.of(new TestMasterData()));

        readerWriter.replaceMasterDataQueue(null);

        assertTrueQueueIsEmpty(readerWriter);
    }

    @Test
    void resetReceiveState_clearsBufferAndSynCount() {
        EbusReaderWriter readerWriter = newReaderWriter();

        getBuffer(readerWriter).writeBytes(new byte[] { 0x01, 0x02, 0x03 });

        ReflectionTestUtils.setField(readerWriter, "synCount", 5);

        ReflectionTestUtils.invokeMethod(readerWriter, "resetReceiveState");

        assertEquals(0, getBuffer(readerWriter).size());
        assertEquals(0, ReflectionTestUtils.getField(readerWriter, "synCount"));
    }

    @Test
    void shutdown_stopsReaderWriter_andClosesConnectionResources() throws Exception {
        EbusReaderWriter readerWriter = newReaderWriter();

        InputStream in = mock(InputStream.class);
        OutputStream out = mock(OutputStream.class);
        Socket socket = mock(Socket.class);

        ReflectionTestUtils.setField(readerWriter, "in", in);
        ReflectionTestUtils.setField(readerWriter, "out", out);
        ReflectionTestUtils.setField(readerWriter, "socket", socket);
        ReflectionTestUtils.setField(readerWriter, "running", true);

        readerWriter.shutdown();

        assertFalse((Boolean) ReflectionTestUtils.getField(readerWriter, "running"));

        verify(in).close();
        verify(out).close();
        verify(socket).close();

        assertNull(ReflectionTestUtils.getField(readerWriter, "in"));

        assertNull(ReflectionTestUtils.getField(readerWriter, "out"));

        assertNull(ReflectionTestUtils.getField(readerWriter, "socket"));
    }

    private static EbusReaderWriter newReaderWriter() {
        EbusReaderWriter readerWriter = new EbusReaderWriter();

        ReflectionTestUtils.setField(readerWriter, "syncBytesBetweenTelegrams", 5);

        return readerWriter;
    }

    @SuppressWarnings("unchecked")
    private static Queue<MasterData> getQueue(EbusReaderWriter readerWriter) {

        return (Queue<MasterData>) ReflectionTestUtils.getField(readerWriter, "masterDataQueue");
    }

    private static void replaceQueue(EbusReaderWriter readerWriter, List<MasterData> data) {

        readerWriter.replaceMasterDataQueue(data);
    }

    private static ByteArrayOutputStream getBuffer(EbusReaderWriter readerWriter) {

        return (ByteArrayOutputStream) ReflectionTestUtils.getField(readerWriter, "buffer");
    }

    private static void assertTrueQueueIsEmpty(EbusReaderWriter readerWriter) {

        assertEquals(0, getQueue(readerWriter).size());
    }

    private static class TestMasterData implements MasterData {

        private byte[] masterData = { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06 };

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

        private byte[] slaveData = { ACK, 0x00, 0x00, 0x00 };

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