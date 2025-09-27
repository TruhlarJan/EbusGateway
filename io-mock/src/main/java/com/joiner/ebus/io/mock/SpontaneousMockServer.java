package com.joiner.ebus.io.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

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
