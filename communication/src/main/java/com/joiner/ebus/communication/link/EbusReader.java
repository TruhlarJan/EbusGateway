package com.joiner.ebus.communication.link;

import static com.joiner.ebus.communication.link.DataEventFactory.ADDRESS_SIZE;
import static com.joiner.ebus.communication.protherm.MasterData.SYN;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EbusReader {

    @Value("${adapter.host:127.0.0.1}")
    private String host;

    @Value("${adapter.port.listen:3334}")
    private int port;

    @Value("${ebus.read.timeout:2000}")
    private int readTimeout;

    @Value("${ebus.read.watchdog.interval:30000}")
    private int watchdogInterval;

    @Value("${ebus.read.reconnect.pause:2000}")
    private int reconnectPause;

    private Socket socket;
    private InputStream in;
    private volatile boolean running = true;

    private final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    /* max size of byte array output stream */
    private static final int MAX_SIZE = 64;

    @Autowired
    @Getter
    private DataEventFactory dataEventFactory;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void start() {
        Thread t = new Thread(this::readLoop, "Ebus-DataListener");
        t.setDaemon(true);
        t.start();
    }

    private void connect() throws InterruptedException {
        int attempt = 0;
        while (running) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(readTimeout); // čtení po bytech s configurable timeoutem
                in = socket.getInputStream();
                log.info("Connected to eBUS server at {}:{}", socket.getInetAddress(), socket.getPort());
                Thread.sleep(reconnectPause); // pauza po reconnectu
                break;
            } catch (Exception e) {
                attempt++;
                int wait = Math.min(100 * attempt, 2000);
                if (attempt % 5 == 0) {
                    log.warn("Still waiting for eBUS server at {}:{} after {} attempts", host, port, attempt, e);
                }
                Thread.sleep(wait);
            }
        }
    }

    private void readLoop() {
        long lastDataTime = System.currentTimeMillis();
        int reconnectAttempt = 0;

        while (running) {
            try {
                if (socket == null || socket.isClosed() || !socket.isConnected()) {
                    connect();
                    lastDataTime = System.currentTimeMillis();
                    reconnectAttempt = 0;
                }

                int b;
                try {
                    b = in.read();
                } catch (java.net.SocketTimeoutException ste) {
                    if (System.currentTimeMillis() - lastDataTime > watchdogInterval) {
                        throw new java.io.IOException(
                                "No data from adapter for " + watchdogInterval + " ms – assuming dead connection");
                    }
                    continue;
                }

                if (b == -1)
                    throw new java.io.EOFException("End of stream reached");

                lastDataTime = System.currentTimeMillis();
                processByte(b);

            } catch (Exception e) {
                if (running) {
                    reconnectAttempt++;
                    int delay = Math.min(500 * (1 << (reconnectAttempt - 1)), 30000);
                    if (reconnectAttempt % 5 == 0) {
                        log.warn("Lost connection to {}:{}, retrying in {} ms (attempt {})", host, port, delay,
                                reconnectAttempt, e.toString());
                    }
                    closeConnection();
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        }
    }

    private void processByte(int b) {
        if (b == SYN) {
            if (byteArrayOutputStream.size() >= ADDRESS_SIZE) {
                try {
                    byte[] data = byteArrayOutputStream.toByteArray();
                    publisher.publishEvent(dataEventFactory.getDataReadyEvent(data));
                } catch (Exception ex) {
                    log.warn("Frame parse error: {}", ex.getMessage());
                }
            }
            byteArrayOutputStream.reset();
        } else {
            byteArrayOutputStream.write(b);
            if (byteArrayOutputStream.size() > MAX_SIZE) {
                log.warn("Frame buffer overflow, resetting");
                byteArrayOutputStream.reset();
            }
        }
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        closeConnection();
    }

    private void closeConnection() {
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
        in = null;
        socket = null;
    }
}
