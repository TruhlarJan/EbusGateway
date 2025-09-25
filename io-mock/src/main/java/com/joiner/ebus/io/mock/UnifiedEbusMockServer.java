package com.joiner.ebus.io.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class UnifiedEbusMockServer {

    private static final int PORT = 3333;

    private final ReentrantLock commLock = new ReentrantLock();
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private final List<byte[]> spontaneousPackets = new ArrayList<>();
    private int packetIndex = 0;
    private final Random random = new Random();
    private final Map<String, byte[]> addressToSlave = new HashMap<>();
    private boolean schedulerEnabled = true;

    public UnifiedEbusMockServer() {
        // Spontánní pakety
        String[] packets = {
            "03 15 B5 13 03 06 00 00 0E",
            "03 15 B5 13 03 06 64 00 63",
            "03 64 B5 12 02 02 00 66",
            "03 64 B5 12 02 02 64 02",
            "03 64 B5 12 02 02 FE 98"
        };
        for (String hex : packets) spontaneousPackets.add(hexToBytes(hex));

        // Jednorázové odpovědi podle adresy
        addressToSlave.put("10 08 B5 10", new byte[]{0x00, 0x01, 0x01, (byte) 0x9A});
    }

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log.info("UnifiedEbusMock listening on port {}", PORT);

                while (true) {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    log.info("Client connected: {}", client.getInetAddress());

                    // Pro každý klient spustíme zpracování master–slave transakce
                    new Thread(() -> handleOneTimeTransaction(client)).start();
                }
            } catch (Exception e) {
                log.error("Server error", e);
            }
        }, "UnifiedEbusMockServerThread").start();
    }

    public void setSchedulerEnabled(boolean enabled) {
        this.schedulerEnabled = enabled;
    }

    @Scheduled(fixedRate = 3000)
    public void scheduledSend() {
        if (!schedulerEnabled || clients.isEmpty()) return;

        if (!commLock.tryLock()) {
            log.debug("Skipping spontaneous packet, master transaction in progress.");
            return;
        }

        try {
            byte[] packet = spontaneousPackets.get(packetIndex);
            packetIndex = (packetIndex + 1) % spontaneousPackets.size();

            for (Socket client : clients) {
                if (client.isClosed() || !client.isConnected()) {
                    disconnect(client);
                    continue;
                }
                try {
                    int aaCount = 5 + random.nextInt(26);
                    byte[] prefix = new byte[aaCount];
                    Arrays.fill(prefix, (byte) 0xAA);

                    byte[] frame = new byte[prefix.length + packet.length];
                    System.arraycopy(prefix, 0, frame, 0, prefix.length);
                    System.arraycopy(packet, 0, frame, prefix.length, packet.length);

                    OutputStream out = client.getOutputStream();
                    for (byte b : frame) {
                        out.write(b);
                        out.flush();
                        Thread.sleep(4, 170_000);
                    }
                } catch (Exception e) {
                    log.warn("Client {} disconnected during send, removing.", client.getInetAddress());
                    disconnect(client);
                }
            }

            log.info("Sent scheduled spontaneous packet");
        } finally {
            commLock.unlock();
        }
    }

    public void handleOneTimeTransaction(Socket client) {
        if (client == null || client.isClosed() || !client.isConnected()) return;

        try (InputStream in = client.getInputStream(); OutputStream out = client.getOutputStream()) {

            // Start byte (bez zámku)
            out.write(0xAA);
            out.flush();

            // Čtení blokující, nezamykáme
            byte[] addr = in.readNBytes(4);
            String addrHex = bytesToHex(addr);

            int len = in.read();
            byte[] masterFrame = in.readNBytes(len + 1);

            byte[] slave = addressToSlave.getOrDefault(addrHex, new byte[]{0x00});

            byte[] response = new byte[addr.length + 1 + masterFrame.length + slave.length];
            System.arraycopy(addr, 0, response, 0, addr.length);
            response[addr.length] = (byte) len;
            System.arraycopy(masterFrame, 0, response, addr.length + 1, masterFrame.length);
            System.arraycopy(slave, 0, response, addr.length + 1 + masterFrame.length, slave.length);

            // Zámek jen při odeslání, aby scheduler mohl posílat spontánní pakety
            commLock.lock();
            try {
                out.write(response);
                out.flush();
            } finally {
                commLock.unlock();
            }

            // Čtení finálních bajtů od mastera (blokující, bez zámku)
            byte[] finalBytes = in.readNBytes(2);
            log.info("Master–slave transaction finished with final bytes: {}", bytesToHex(finalBytes));

        } catch (Exception e) {
            log.error("Transaction error with client {}: {}", client.getInetAddress(), e.getMessage());
            disconnect(client);
        }
    }

    private void disconnect(Socket client) {
        try {
            client.close();
        } catch (Exception ignored) {}
        clients.remove(client);
        log.info("Client disconnected: {}", client.getInetAddress());
    }

    private byte[] hexToBytes(String hex) {
        String[] tokens = hex.split("\\s+");
        byte[] out = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++) out[i] = (byte) Integer.parseInt(tokens[i], 16);
        return out;
    }

    private String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) sb.append(String.format("%02X ", b));
        return sb.toString().trim();
    }
}
