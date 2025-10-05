package com.joiner.ebus.communication.link;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterSlaveData;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EbusWriter {

    @Value("${adapter.host:172.20.10.3}")
    private String host;

    @Value("${adapter.port.raw:3333}")
    private int port;

    @Value("${ebus.write.timeout:2000}")
    private int writeTimeout;

    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private final ReentrantLock lock = new ReentrantLock();

    private final long SYN_TIMEOUT_MS = 15000; // čekání na SYN po reconnectu
    private final int MAX_RETRIES = 3;

    @PostConstruct
    public void init() {
        connect();
    }

    private void connect() {
        int attempt = 0;
        while (true) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(writeTimeout); // jen pro čtení
                out = socket.getOutputStream();
                in = socket.getInputStream();
                log.info("Connected to eBUS server at {}:{}", socket.getInetAddress(), socket.getPort());
                break;
            } catch (Exception e) {
                attempt++;
                int wait = Math.min(100 * attempt, 2000);
                try {
                    Thread.sleep(wait);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Send a master frame, waiting for SYN and retrying if necessary.
     */
    public void sendFrame(MasterSlaveData masterSlaveData) {
        lock.lock();
        try {
            for (int retry = 0; retry < MAX_RETRIES; retry++) {
                try {
                    ensureConnected();
                    waitForSyn();
                    out.write(masterSlaveData.getMasterData());
                    out.flush();
                    return; // úspěšně odesláno
                } catch (Exception e) {
                    log.warn("Attempt {} failed, eBUS server at {}:{} reconnecting...", retry + 1, host, port,  e.getMessage());
                    closeConnection();
                    connect();
                }
            }
            log.error("Frame could not be sent after {} retries", MAX_RETRIES);
        } finally {
            lock.unlock();
        }
    }

    private void ensureConnected() throws Exception {
        if (socket == null || socket.isClosed() || !socket.isConnected()) {
            closeConnection();
            connect();
        }
    }

    private void waitForSyn() throws Exception {
        int b;
        long start = System.currentTimeMillis();
        do {
            b = in.read();
            if (b == -1)
                throw new RuntimeException("Connection closed before receiving SYN");
            if (System.currentTimeMillis() - start > SYN_TIMEOUT_MS) {
                throw new RuntimeException("Timeout waiting for SYN");
            }
        } while (b != MasterSlaveData.SYN);
    }

    @PreDestroy
    public void shutdown() {
        closeConnection();
    }

    private void closeConnection() {
        try {
            if (out != null)
                out.close();
        } catch (Exception ignored) {
        }
        try {
            if (in != null)
                in.close();
        } catch (Exception ignored) {
        }
        try {
            if (socket != null)
                socket.close();
        } catch (Exception ignored) {
        }
        out = null;
        in = null;
        socket = null;
    }
}
