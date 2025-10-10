package com.joiner.ebus.io.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MasterSlaveMockServer {

    private static final int PORT = 3333;
    private volatile boolean running = true;
    private Map<Long, byte[]> map = new HashMap<>();

    public MasterSlaveMockServer() {
        map.put(17629583509760L, new byte[] { 0x00, 0x01, 0x01, (byte) 0x9A });
        map.put(17629583573248L, new byte[] { 0x00, 0x08, 0x50, 0x02, 0x0C, 0x00, 0x1F, 0x10, 0x00, (byte) 0x80, (byte) 0x07 });
        map.put(17629583573249L, new byte[] { 0x00, 0x09, 0x4a, 0x46, 0x00, (byte) 0x80, (byte) 0xFF, 0x5c, 0x00, 0x00, (byte) 0xFF, (byte) 0xB0 });
        map.put(17629583573250L, new byte[] { 0x00, 0x05, 0x02, 0x14, (byte) 0x96, 0x5A, 0x78, 0x0D });
    }

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log.info("MasterSlaveMockServer listening on port {}", PORT);

                while (running) {
                    Socket client = serverSocket.accept();
                    client.setSoTimeout(0);
                    log.info("Client connected: {}", client.getInetAddress());
                    new Thread(() -> handleTransaction(client)).start();
                }
            } catch (Exception e) {
                if (running)
                    log.error("Server error", e);
            }
        }, "MasterSlaveMockServerThread").start();
    }

    private void handleTransaction(Socket client) {
        try (InputStream in = client.getInputStream(); OutputStream out = client.getOutputStream()) {
            out.write(0xAA);
            out.flush();

            byte[] masterData = in.readAllBytes();
            if (masterData.length >= 6) {
                byte[] address = Arrays.copyOf(masterData, 6);
                long key = getKey(address);
                byte[] slaveData = map.get(key);
                log.info("Data: {} {}", bytesToHex(masterData), bytesToHex(slaveData));
            }
        } catch (Exception e) {
            log.error("Transaction error with client {}: {}", client.getInetAddress(), e.getMessage());
        } finally {
            try {
                client.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void stop() {
        running = false;
    }

    private String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data)
            sb.append(String.format("%02X ", b));
        return sb.toString().trim();
    }

    private long getKey(byte[] byteArray) {
        long key = 0;
        for (int i = 0; i < 6; i++) {
            key = (key << 8) | (byteArray[i] & 0xFFL);
        }
        return key;
    }

}
