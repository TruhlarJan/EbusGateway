package com.joiner.ebus.communication.link;

import static com.joiner.ebus.communication.ByteUtils.isAllZero;
import static com.joiner.ebus.communication.link.FrameParser.ADDRESS_SIZE;
import static com.joiner.ebus.communication.protherm.MasterSlaveData.SYN;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterSlaveData;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EbusSlaveMasterLink {
    
    @Value("${adapter.host:127.0.0.1}")
    private String host;

    @Value("${adapter.port.listen:3334}")
    private int port;

    private Socket socket;
    private InputStream in;
    private volatile boolean running = true;

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    
    @Autowired
    @Getter
    private FrameParser frameParser;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void start() {
        new Thread(this::readLoop, "DataListenerThread").start();
    }

    private void connect() throws InterruptedException {
        int attempt = 0;
        while (running) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(0); // blokující read
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

    private void readLoop() {
        while (running) {
            try {
                if (socket == null || socket.isClosed() || !socket.isConnected()) {
                    connect();
                }
                int b = in.read(); // blokuje dokud není byte k dispozici
                processByte(b);
            } catch (Exception e) {
                if (running) {
                    log.warn("Lost connection to {}:{}, will retry...", host, port, e);
                    try { 
                        if (in != null) in.close(); 
                        if (socket != null) socket.close(); 
                    } catch (Exception ignored) {}
                    socket = null;
                    in = null;
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                }
            }
        }
    }

    private void processByte(int b) {
        if (b != -1 && b != SYN) {
            byteArrayOutputStream.write(b);

            if (byteArrayOutputStream.size() > 1024) {
                log.warn("StringBuilder overflow, resetting");
                byteArrayOutputStream.reset();
            }
        }
        if (byteArrayOutputStream.size() >= ADDRESS_SIZE && b == SYN) {
            MasterSlaveData masterSlaveData = frameParser.getMasterSlaveData(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.reset();
            if (!isAllZero(masterSlaveData.getSlaveData())) {
                publisher.publishEvent(new MasterSlaveDataReadyEvent(this, masterSlaveData));
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        try {
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (Exception ignored) {}
    }
}
