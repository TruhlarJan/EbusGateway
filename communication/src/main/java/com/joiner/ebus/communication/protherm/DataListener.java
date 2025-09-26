package com.joiner.ebus.communication.protherm;

import java.io.InputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataListener {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 3334;

    private StringBuilder stringBuilder = new StringBuilder();
    private ReentrantLock lock;

    private Socket socket;
    private InputStream in;
    private volatile boolean running = true;

    @PostConstruct
    public void start() {
        new Thread(this::readLoop, "DataListenerThread").start();
    }

    public void setLock(ReentrantLock lock) {
        this.lock = lock;
    }

    private void init() throws Exception {
        if (socket == null) {
            while (running) {
                try {
                    socket = new Socket(HOST, PORT);
                    socket.setSoTimeout(0); // blokující read
                    in = socket.getInputStream();
                    log.info("Connected to eBUS mock server at {}:{}", HOST, PORT);
                    break;
                } catch (Exception e) {
                    log.info("Waiting for eBUS mock server on {}:{}...", HOST, PORT);
                    Thread.sleep(100);
                }
            }
        }
    }

    private void readLoop() {
        try {
            init();
            while (running) {
                int b = in.read(); // blokuje dokud není byte k dispozici
                if (b != -1) {
                    lock.lock();
                    try {
                        if (b != OperationalData.SYN) {
                            stringBuilder.append(String.format("%02X", b));
                        }
                        if (!stringBuilder.isEmpty() && b == OperationalData.SYN) {
                            // process data
                            log.info("Data: {}", stringBuilder.toString());
                            stringBuilder.setLength(0);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        } catch (Exception e) {
            if (running) {
                log.error("Error in readLoop", e);
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
