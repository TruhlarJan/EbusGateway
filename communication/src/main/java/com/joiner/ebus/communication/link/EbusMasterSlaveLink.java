package com.joiner.ebus.communication.link;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterSlaveData;

@Component
public class EbusMasterSlaveLink {

    @Value("${adapter.host:127.0.0.1}")
    private String host;

    @Value("${adapter.port.raw:3333}")
    private int port;

    @Autowired
    private ApplicationEventPublisher publisher;

    private ReentrantLock lock = new ReentrantLock();

    
    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    public void sendFrame(MasterSlaveData masterSlaveData) throws Exception {
        lock.lock();
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(2000); // timeout 2s

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // -------------------------------
            // čekáme na první SYN (0xAA) - ignorujeme přebytečné SYNy
            // -------------------------------
            int readByte;
            do {
                readByte = in.read();
                if (readByte == -1) {
                    throw new RuntimeException("Connection closed before receiving SYN");
                }
            } while (readByte != MasterSlaveData.SYN);

            // -------------------------------
            // pošleme master rámec po bytech
            // -------------------------------
            byte[] masterFrame = masterSlaveData.getMasterStartData();
            for (byte b : masterFrame) {
                out.write(b & 0xFF);
                out.flush();
                Thread.sleep(4, 170_000); // simulace 2400 Bd
            }

            // -------------------------------
            // načteme master echo po bytech
            // -------------------------------
            byte[] masterEcho = new byte[masterFrame.length];
            int read = 0;
            while (read < masterEcho.length) {
                int r = in.read(masterEcho, read, masterEcho.length - read);
                if (r == -1) {
                    throw new RuntimeException("Connection closed while reading master echo");
                }
                read += r;
            }

            // -------------------------------
            // načteme slave odpověď přesně podle velikosti
            // -------------------------------
            byte[] slaveResponse = new byte[masterSlaveData.getSlaveSize()];
            read = 0;
            while (read < slaveResponse.length) {
                int r = in.read(slaveResponse, read, slaveResponse.length - read);
                if (r == -1) {
                    throw new RuntimeException("Connection closed before receiving slave response");
                }
                read += r;
            }

            // -------------------------------
            // pošleme ACK (a SYN) po bytech
            // -------------------------------
            byte[] ack = masterSlaveData.getMasterFinalData();
            for (byte b : ack) {
                out.write(b & 0xFF);
                out.flush();
                Thread.sleep(4, 170_000); // simulace 2400 Bd
            }

            masterSlaveData.setSlaveData(slaveResponse);
            publisher.publishEvent(new MasterSlaveDataReadyEvent(this, masterSlaveData));
        } finally {
            lock.unlock();
        }
    }

}
