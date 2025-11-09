package com.joiner.ebus.communication.link;

import static com.joiner.ebus.communication.protherm.MasterData.SYN;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EbusReaderWriter {

    @Value("${ebus.adapter.host:127.0.0.1}")
    private String host;

    @Value("${ebus.adapter.port:3333}")
    private int port;

    @Value("${ebus.timeout:2000}")
    private int readTimeout;

    @Value("${ebus.watchdog.interval:30000}")
    private int watchdogInterval;

    @Value("${ebus.reconnect.pause:2000}")
    private int reconnectPause;

    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private volatile boolean running = true;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private static final int MAX_SIZE = 64;

    @Getter
    private Queue<MasterData> masterDataQueue = new ArrayDeque<>();

    @Autowired	
    private DataParser dataParser;
    
    @Autowired
    @Getter
    private DataEventFactory dataEventFactory;

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void start() {
        Thread t = new Thread(this::readWriteLoop, "readWriteLoop");
        t.setDaemon(true);
        t.start();
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        closeConnection();
    }

    private void connect() throws InterruptedException {
        int attempt = 0;
        while (running) {
            try {
                socket = new Socket(host, port);
                socket.setSoTimeout(readTimeout);
                in = socket.getInputStream();
                out = socket.getOutputStream();
                log.info("Connected to eBUS server at {}:{}", socket.getInetAddress(), socket.getPort());
                Thread.sleep(reconnectPause);
                break;
            } catch (Exception e) {
                attempt++;
                int wait = Math.min(100 * attempt, 2000);
                if (attempt % 5 == 0) {
                    log.warn("Still waiting for eBUS server at {}:{} after {} attempts. {}", host, port, attempt, e.getMessage());
                }
                Thread.sleep(wait);
            }
        }
    }

    private void readWriteLoop() {
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
                        throw new IOException("No data from adapter for " + watchdogInterval + " ms – assuming dead connection");
                    }
                    continue;
                }
                if (b == -1) {
                    throw new EOFException("End of stream reached");
                }
                lastDataTime = System.currentTimeMillis();

                if (b == SYN) {
                    if (!masterDataQueue.isEmpty()) {
                        sendMasterData();
                    } else {
                        processMasterData();
                    }
                } else {
                    processByte(b);
                }
            } catch (Exception e) {
                if (running) {
                    reconnectAttempt++;
                    int delay = Math.min(500 * (1 << (reconnectAttempt - 1)), 30000);
                    if (reconnectAttempt % 5 == 0) {
                        log.warn("Lost connection to {}:{}, retrying in {} ms (attempt {})", host, port, delay, reconnectAttempt, e.toString());
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

    private void sendMasterData() throws IOException {
        MasterData data = masterDataQueue.poll();
        out.write(data.getMasterData());
        out.flush();
    }

    private void processMasterData() {
        byte[] data = buffer.toByteArray();
        MasterData masterData = dataParser.getMasterData(data);
		if (masterData != null) {
			publisher.publishEvent(dataEventFactory.getDataReadyEvent(masterData));
	        buffer.reset();
		}
    }

	private void processByte(int b) {
        buffer.write(b);
        byte[] data = buffer.toByteArray();
        MasterSlaveData masterSlaveData = dataParser.getMasterSlaveData(data);
		if (masterSlaveData != null) {
			publisher.publishEvent(dataEventFactory.getDataReadyEvent(masterSlaveData));
			buffer.reset();
		}
        if (buffer.size() > MAX_SIZE) {
            log.warn("Frame buffer overflow, resetting");
            buffer.reset();
        }
    }

    private void closeConnection() {
        try {
            if (in != null) {
				in.close();
			}
            if (out != null) {
				out.close();
			}
        } catch (Exception ignored) {
        }
        try {
            if (socket != null) {
				socket.close();
			}
        } catch (Exception ignored) {
        }
        in = null;
        out = null;
        socket = null;
    }
}
