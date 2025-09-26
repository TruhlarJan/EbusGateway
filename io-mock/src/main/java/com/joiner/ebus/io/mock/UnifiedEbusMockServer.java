package com.joiner.ebus.io.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ebus mock server sends spontaneous packets with the correct prefix 0xAA (really limited to 256 bytes, as the boiler does),
 * respects master–slave transactions and stops spontaneous operation during them (realistic eBUS simulation),
 * can be terminated in tests via stop(), so it won't hang,
 * and responds according to the address map (addressToSlave), so you can extend the response logic as needed.
 */
@Service
@Slf4j
public class UnifiedEbusMockServer {

    private static final int PORT = 3333;

    private final ReentrantLock commLock = new ReentrantLock();
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private final List<byte[]> spontaneousPackets = new ArrayList<>();
    private final Random random = new Random();
    private final Map<String, byte[]> addressToSlave = new HashMap<>();
    private volatile boolean running = true; // flag pro ukončení threadů

    public UnifiedEbusMockServer() {
        // Spontánní pakety
        String[] packets = {
            "03 15 B5 13 03 06 00 00 0E",
            "03 15 B5 13 03 06 64 00 63",
            "03 64 B5 12 02 02 00 66",
            "03 64 B5 12 02 02 64 02",
            "03 64 B5 12 02 02 FE 98"
        };
        for (String hex : packets)
            spontaneousPackets.add(hexToBytes(hex));

        // Jednorázové odpovědi podle adresy
        addressToSlave.put("10 08 B5 10", new byte[]{0x00, 0x01, 0x01, (byte) 0x9A});
    }

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log.info("UnifiedEbusMockServer listening on port {}", PORT);

                while (running) {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    log.info("Client connected: {}", client.getInetAddress());

                    // Spuštění duplexní obsluhy
                    new Thread(() -> handleOneTimeTransaction(client)).start();

                    // Spuštění threadu pro spontánní pakety
                    new Thread(() -> sendSpontaneousPackets(client)).start();
                }
            } catch (Exception e) {
                if (running)
                    log.error("Server error", e);
            }
        }, "UnifiedEbusMockServerThread").start();
    }

    /**
     * Spontaneous packet sending.
     * @param client
     */
    private void sendSpontaneousPackets(Socket client) {
        try (OutputStream out = client.getOutputStream()) {
            Thread.sleep(1000);
            log.info("Sending spontaneous packets");

            int index = 0;
            while (!client.isClosed() && running) {
                byte[] packet = spontaneousPackets.get(index);
                index = (index + 1) % spontaneousPackets.size();

                for (Socket c : clients) {
                    if (c.isClosed() || !c.isConnected()) {
                        disconnect(c);
                        continue;
                    }

                    OutputStream o = c.getOutputStream();

                    // Realistický prefix 0xAA – max 256 bajtů
                    commLock.lockInterruptibly();
                    try {
                        int aaCount = 6 + random.nextInt(251); // 6–256 bajtů
                        byte[] prefix = new byte[aaCount];
                        Arrays.fill(prefix, (byte) 0xAA);

                        for (byte b : prefix) {
                            o.write(b);
                            o.flush();
                            Thread.sleep(4, 170_000);
                        }

                        for (byte b : packet) {
                            o.write(b);
                            o.flush();
                            Thread.sleep(4, 170_000);
                        }
                    } finally {
                        commLock.unlock();
                    }
                }
            }
        } catch (Exception e) {
            if (running)
                log.warn("Spontaneous packet sending error: {}", e.getMessage());
            disconnect(client);
        }
    }

    /**
     * Master-slave transaction.
     * @param client
     */
    private void handleOneTimeTransaction(Socket client) {
        if (client == null || client.isClosed() || !client.isConnected())
            return;

        try (InputStream in = client.getInputStream(); OutputStream out = client.getOutputStream()) {

            out.write(0xAA);
            out.flush();

            byte[] addr = in.readNBytes(4);
            String addrHex = bytesToHex(addr);

            int len = in.read();
            byte[] masterFrame = in.readNBytes(len + 1);

            byte[] slave = addressToSlave.getOrDefault(addrHex, new byte[] { 0x00 });

            byte[] response = new byte[addr.length + 1 + masterFrame.length + slave.length];
            System.arraycopy(addr, 0, response, 0, addr.length);
            response[addr.length] = (byte) len;
            System.arraycopy(masterFrame, 0, response, addr.length + 1, masterFrame.length);
            System.arraycopy(slave, 0, response, addr.length + 1 + masterFrame.length, slave.length);

            commLock.lock();
            try {
                for (byte b : response) {
                    out.write(b);
                    out.flush();
                    Thread.sleep(4, 170_000);
                }
            } finally {
                commLock.unlock();
            }

            // Čtení finálních bajtů od mastera
            byte[] finalBytes = in.readNBytes(2);
            log.info("Master–slave transaction finished with final bytes: {}", bytesToHex(finalBytes));

        } catch (Exception e) {
            log.error("Transaction error with client {}: {}", client.getInetAddress(), e.getMessage());
            disconnect(client);
        }
    }

    public void stop() {
        running = false;
        for (Socket c : clients)
            disconnect(c);
        clients.clear();
        log.info("UnifiedEbusMockServer stopped");
    }

    private void disconnect(Socket client) {
        try {
            client.close();
        } catch (Exception ignored) {
        }
        clients.remove(client);
        log.info("Client disconnected: {}", client.getInetAddress());
    }

    private byte[] hexToBytes(String hex) {
        String[] tokens = hex.split("\\s+");
        byte[] out = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            out[i] = (byte) Integer.parseInt(tokens[i], 16);
        return out;
    }

    private String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data)
            sb.append(String.format("%02X ", b));
        return sb.toString().trim();
    }
}
