package com.joiner.ebus.communication.link;

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
        Thread t = new Thread(this::readLoop, "Ebus-DataListener");
        t.setDaemon(true);
        t.start();
    }

    private void connect() throws InterruptedException {
        int attempt = 0;
        while (running) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(15000); // timeout pro read
                in = socket.getInputStream();
                log.info("Connected to eBUS server at {}:{}", socket.getInetAddress(), socket.getPort());

                // nech adapter rozjet stream
                Thread.sleep(2000);
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

        while (running) {
            try {
                if (socket == null || socket.isClosed() || !socket.isConnected()) {
                    connect();
                    lastDataTime = System.currentTimeMillis();
                }

                int b;
                try {
                    b = in.read(); // blokuje max 15 s
                } catch (java.net.SocketTimeoutException ste) {
                    if (System.currentTimeMillis() - lastDataTime > 30000) {
                        throw new java.io.IOException("No data from adapter for 30s – assuming dead connection");
                    }
                    continue;
                }

                if (b == -1) {
                    throw new java.io.EOFException("End of stream reached");
                }

                lastDataTime = System.currentTimeMillis();
                processByte(b);

            } catch (Exception e) {
                if (running) {
                    log.warn("Lost connection to {}:{}, will retry...", host, port, e.toString());
                    closeConnection();
                    try {
                        Thread.sleep(2000); // pauza před reconnectem
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
                    MasterSlaveData frame = frameParser.getMasterSlaveData(byteArrayOutputStream.toByteArray());
                    publisher.publishEvent(new MasterSlaveDataReadyEvent(this, frame));
                } catch (Exception ex) {
                    log.warn("Frame parse error: {}", ex.getMessage());
                }
            }
            byteArrayOutputStream.reset();
        } else {
            byteArrayOutputStream.write(b);
            if (byteArrayOutputStream.size() > 64) {
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
