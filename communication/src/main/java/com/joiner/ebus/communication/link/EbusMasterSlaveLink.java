package com.joiner.ebus.communication.link;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterSlaveData;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EbusMasterSlaveLink {

    @Value("${adapter.host:172.20.10.3}")
    private String host;

    @Value("${adapter.port.raw:3333}")
    private int port;
    
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private volatile boolean running = true;

    private void connect() throws InterruptedException {
        int attempt = 0;
        while (running) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(0); // blokující read
                out = socket.getOutputStream();
                in = socket.getInputStream();
                log.info("Connected to eBUS server at {}:{}", socket.getInetAddress(), socket.getPort());
                break;
            } catch (Exception e) {
                attempt++;
                int wait = Math.min(100 * attempt, 2000); // max 2 s
                if (attempt % 5 == 0) {
                    log.warn("Still waiting for eBUS server at {}:{} after {} attempts", host, port, attempt, e);
                }
                Thread.sleep(wait);
            }
        }
    }

    public void sendFrame(MasterSlaveData masterSlaveData) throws Exception {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            connect();
        }
        try {
            // -------------------------------
            // čekáme na první SYN (0xAA)
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
            out.write(masterSlaveData.getMasterData());
            out.flush();
            Thread.sleep(100);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
