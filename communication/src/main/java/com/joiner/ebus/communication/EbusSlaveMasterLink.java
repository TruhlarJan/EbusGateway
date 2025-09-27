package com.joiner.ebus.communication;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EbusSlaveMasterLink {

    @Value("${adapter.host:127.0.0.1}")
    private String host;

    @Value("${adapter.port.listen:3334}")
    private int port;

    private ReentrantLock lock = new ReentrantLock();

    private Socket socket;
    private InputStream in;
    private volatile boolean running = true;

    private ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    
    @Autowired
    @Getter
    private FrameParser frameParser;

    @PostConstruct
    public void start() {
        new Thread(this::readLoop, "DataListenerThread").start();
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    private void connect() throws InterruptedException {
        int attempt = 0;
        while (running) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(0); // blokující read
                in = socket.getInputStream();
                log.info("Connected to eBUS mock server at {}", socket.getInetAddress());
                break;
            } catch (Exception e) {
                attempt++;
                int wait = Math.min(100 * attempt, 2000); // max 2 s
                log.info("Waiting {} ms for eBUS mock server... attempt {}", wait, attempt);
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

                lock.lock();
                try {
                    int b = in.read(); // blokuje dokud není byte k dispozici
                    processByte(b);
                } finally {
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
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
        if (b != -1 && b != MasterSlaveData.SYN) {
            byteArrayOutputStream.write(b);

            if (byteArrayOutputStream.size() > 1024) {
                log.warn("StringBuilder overflow, resetting");
                byteArrayOutputStream.reset();
            }
        }
        if (byteArrayOutputStream.size() > 0 && b == MasterSlaveData.SYN) {
            frameParser.save(byteArrayOutputStream.toByteArray());
            byteArrayOutputStream.reset();
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
