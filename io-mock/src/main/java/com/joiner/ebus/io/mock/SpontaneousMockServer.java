package com.joiner.ebus.io.mock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
                    new Thread(() -> sendSpontaneousPackets(client)).start();
                }
            } catch (Exception e) {
                if (running) log.error("Server error", e);
            }
        }, "SpontaneousMockServerThread").start();
    }

    private void sendSpontaneousPackets(Socket client) {
        try (OutputStream out = client.getOutputStream()) {
            Thread.sleep(1000);

            int index = 0;
            while (!client.isClosed() && running) {
                byte[] packet = spontaneousPackets.get(index);
                int aaCount = 6 + random.nextInt(251); // 6–256 bajtů
                byte[] prefix = new byte[aaCount];
                Arrays.fill(prefix, (byte) 0xAA);

                for (byte b : prefix) {
                    out.write(b);
                    out.flush();
                    Thread.sleep(4, 170_000);
                }

                for (byte b : packet) {
                    out.write(b);
                    out.flush();
                    Thread.sleep(4, 170_000);
                }
            }
        } catch (Exception e) {
            log.warn("Spontaneous packet sending error: {}", e.getMessage());
        } finally {
            try { client.close(); } catch (Exception ignored) {}
            clients.remove(client);
        }
    }

    public void stop() { running = false; }

    private byte[] hexToBytes(String hex) {
        String[] tokens = hex.split("\\s+");
        byte[] out = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            out[i] = (byte) Integer.parseInt(tokens[i], 16);
        return out;
    }
}
