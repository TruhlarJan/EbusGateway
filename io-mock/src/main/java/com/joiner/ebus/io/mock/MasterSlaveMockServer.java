package com.joiner.ebus.io.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MasterSlaveMockServer {

    private static final int PORT = 3333;
    private final Map<String, byte[]> addressToSlave = new HashMap<>();
    private volatile boolean running = true;

    public MasterSlaveMockServer() {
        // Jednorázové odpovědi podle adresy
        addressToSlave.put("10 08 B5 10", new byte[]{0x00, 0x01, 0x01, (byte) 0x9A});
    }

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log.info("MasterSlaveMockServer listening on port {}", PORT);

                while (running) {
                    Socket client = serverSocket.accept();
                    log.info("Client connected: {}", client.getInetAddress());
                    new Thread(() -> handleTransaction(client)).start();
                }
            } catch (Exception e) {
                if (running) log.error("Server error", e);
            }
        }, "MasterSlaveMockServerThread").start();
    }

    private void handleTransaction(Socket client) {
        try (InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()) {

            out.write(0xAA);
            out.flush();

            byte[] addr = in.readNBytes(4);
            String addrHex = bytesToHex(addr);
            log.info("Master–slave transaction started with master bytes: {}", addrHex);
            
            int len = in.read();
            byte[] masterFrame = in.readNBytes(len + 1);

            byte[] slave = addressToSlave.getOrDefault(addrHex, new byte[]{0x00});
            byte[] response = new byte[addr.length + 1 + masterFrame.length + slave.length];

            System.arraycopy(addr, 0, response, 0, addr.length);
            response[addr.length] = (byte) len;
            System.arraycopy(masterFrame, 0, response, addr.length + 1, masterFrame.length);
            System.arraycopy(slave, 0, response, addr.length + 1 + masterFrame.length, slave.length);

            for (byte b : response) {
                out.write(b);
                out.flush();
                Thread.sleep(4, 170_000); // simulace 2400 Bd
            }

            byte[] finalBytes = in.readNBytes(2);
            log.info("Master–slave transaction finished with final bytes: {}", bytesToHex(finalBytes));

        } catch (Exception e) {
            log.error("Transaction error with client {}: {}", client.getInetAddress(), e.getMessage());
        } finally {
            try { client.close(); } catch (Exception ignored) {}
        }
    }

    public void stop() { running = false; }

    private String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) sb.append(String.format("%02X ", b));
        return sb.toString().trim();
    }
}
