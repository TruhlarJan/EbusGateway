package com.joiner.ebus.io.mock;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SpontaneousMockServer {

    private static final int PORT = 3334;
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private final List<byte[]> spontaneousPackets = new ArrayList<>();
    private final Random random = new Random();
    private volatile boolean running = true;

    public SpontaneousMockServer() {
        String[] packets = {
            "10 08 B5 10 09 00 00 14 5A FF FF 05 FF 00 47 00 01 01 9A",
            "10 08 B5 11 01 00 88 00 08 50 02 0C 00 1F 10 00 80 07",
            "10 08 B5 11 01 01 89 00 09 4A 46 00 80 FF 5C 00 00 FF B0",
            "10 08 B5 11 01 02 8A 00 05 02 14 96 5A 78 0D",
            "03 15 B5 13 03 06 00 00 0E",
            "03 15 B5 13 03 06 64 00 63",
            "03 64 B5 12 02 02 00 66",
            "03 64 B5 12 02 02 64 02",
            "03 64 B5 12 02 02 FE 98"
        };
        for (String hex : packets) spontaneousPackets.add(hexToBytes(hex));
    }

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log.info("SpontaneousMockServer listening on port {}", PORT);

                while (running) {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    log.info("Client connected: {}", client.getInetAddress());
                    new Thread(() -> sendSpontaneousPackets(client), "MockClientThread-" + client.getPort()).start();
                }

            } catch (Exception e) {
                if (running) log.error("Server error", e);
            }
        }, "SpontaneousMockServerThread").start();
    }

    private void sendSpontaneousPackets(Socket client) {
        try {
            OutputStream out = client.getOutputStream();

            while (!client.isClosed() && running) {
                // Vytvoříme kopii seznamu a zamícháme pořadí
                List<byte[]> frameBatch = new ArrayList<>(spontaneousPackets);
                Collections.shuffle(frameBatch);

                // pošleme jen 5 frame
                for (int i = 0; i < 5; i++) {
                    byte[] packet = frameBatch.get(i);

                    // prefix AA 6–50 bajtů
                    int aaCount = 6 + random.nextInt(45);
                    byte[] prefix = new byte[aaCount];
                    Arrays.fill(prefix, (byte) 0xAA);
                    out.write(prefix);

                    // krátká pauza mezi prefixem a frame
                    Thread.sleep(1 + random.nextInt(4));

                    out.write(packet);
                    out.flush();

                    // pauza 1–2 s mezi frame – realistické tempo eBUS
                    Thread.sleep(1000 + random.nextInt(1000));
                }

                // krátká tichá doba 0–2 s před dalším cyklem
                Thread.sleep(random.nextInt(2000));
            }

        } catch (Exception e) {
            log.warn("Client {} disconnected or error: {}", client.getInetAddress(), e.getMessage());
        } finally {
            try { client.close(); } catch (Exception ignored) {}
            clients.remove(client);
        }
    }

    public void stop() {
        running = false;
        for (Socket client : clients) {
            try { client.close(); } catch (Exception ignored) {}
        }
        clients.clear();
    }

    private byte[] hexToBytes(String hex) {
        String[] tokens = hex.split("\\s+");
        byte[] out = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            out[i] = (byte) Integer.parseInt(tokens[i], 16);
        return out;
    }
}
