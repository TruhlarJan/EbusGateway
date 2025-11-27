package com.joiner.ebus.io.mock;

import java.io.InputStream;
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

/**
 * Mock Protherm Ebus device for testing purposes. It generates spontaneous supported frames.
 * It also responds to master requests by updating its internal state.
 * The first spontaneous frame is treated as the authoritative device state.
 * When a master request is received, the dynamic part of the request updates the authoritative state.
 * Subsequent spontaneous frames include this updated state.
 * The mock server listens on port 3333 and handles multiple clients concurrently.
 * Each client connection spawns a dedicated thread for handling master requests and sending spontaneous frames.
 * The spontaneous frames are sent in random order, with the authoritative state included in the mix.
 * The server can be stopped gracefully by calling the stop() method.
 * Source code has been generated AI.
 * @author joiner
 */
@Service
@Slf4j
public class ProthermEbusMock {

    private static final int PORT = 3333;
    private final List<Socket> clients = new CopyOnWriteArrayList<>();
    private final List<byte[]> spontaneousPackets = new ArrayList<>();
    private final Random random = new Random();
    private volatile boolean running = true;

    private final Object stateLock = new Object();
    private byte[] deviceState; // autoritativní první frame

    // prefix master requestu 10 08 B5 10 09 00
    private final byte[] PREFIX = hexToBytes("10 08 B5 10 09 00");

    public ProthermEbusMock() {
        String[] packets = {
            "10 08 B5 10 09 00 00 14 5A FF FF 05 FF 00 47 00 01 01 9A",
            "10 08 B5 11 01 00 88 00 08 49 02 0C 00 1F 10 00 80 2E",
            "10 08 B5 11 01 01 89 00 09 4A 46 00 80 FF 5C 04 00 FF E8",
            "10 08 B5 11 01 02 8A 00 05 02 14 96 5A 78 0D",
            "03 15 B5 13 03 06 00 00 0E",
            "03 64 B5 12 02 02 00 66"
        };
        for (String hex : packets) spontaneousPackets.add(hexToBytes(hex));

        // nastavíme autoritativní deviceState jako kopii první položky
        synchronized (stateLock) {
            deviceState = spontaneousPackets.get(0).clone();
        }
    }

    @PostConstruct
    public void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                log.info("ProthermEbusMock listening on port {}", PORT);

                while (running) {
                    Socket client = serverSocket.accept();
                    clients.add(client);
                    log.info("Client connected: {}", client.getInetAddress());
                    new Thread(() -> handleClient(client),
                                "MockClientThread-" + client.getPort()).start();
                }

            } catch (Exception e) {
                if (running) log.error("Server error", e);
            }
        }, "ProthermEbusMockThread").start();
    }

    private void handleClient(Socket client) {
        try {
            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();

            // vlastní vlákno posílající spontánní pakety
            new Thread(() -> sendSpontaneousFrames(client, out)).start();

            // čtení a zpracování master packetů
            readMasterAndRespond(client, in, out);

        } catch (Exception e) {
            log.warn("Client {} disconnected or error: {}", client.getInetAddress(), e.getMessage());
        } finally {
            try { client.close(); } catch (Exception ignored) {}
            clients.remove(client);
        }
    }

    /* ---------------------------------------------------------------------- */
    /*  MASTER → SLAVE LOGIKA (úprava prvního rámce podle requestu)         */
    /* ---------------------------------------------------------------------- */
    private void readMasterAndRespond(Socket client, InputStream in, OutputStream out) {
        try {
            byte[] buffer = new byte[256];

            while (!client.isClosed() && running) {
                int len = in.read(buffer);
                if (len <= 0) continue;

                byte[] received = Arrays.copyOf(buffer, len);

                int idx = indexOf(received, PREFIX);
                if (idx < 0) continue;

                int dataStart = idx + PREFIX.length;
                if (received.length < dataStart + 8) {
                    log.warn("Master packet too short!");
                    continue;
                }

                byte[] dynamicPart = Arrays.copyOfRange(received, dataStart, dataStart + 8);

                // atomicky aktualizujeme deviceState (trvalá změna)
                synchronized (stateLock) {
                    System.arraycopy(dynamicPart, 0, deviceState, PREFIX.length, 8);
                }

                // pošli snapshot aktuálního stavu
                byte[] toSend;
                synchronized (stateLock) {
                    toSend = deviceState.clone();
                }

                out.write(new byte[]{(byte) 0xAA, (byte) 0xAA, (byte) 0xAA});
                Thread.sleep(5);
                out.write(toSend);
                out.flush();
            }

        } catch (Exception e) {
            log.warn("Master read error: {}", e.getMessage());
        }
    }

    /* ---------------------------------------------------------------------- */
    /*  SPONTÁNNÍ FRAMES — tvoje původní logika                             */
    /* ---------------------------------------------------------------------- */
    private void sendSpontaneousFrames(Socket client, OutputStream out) {
        try {
            while (!client.isClosed() && running) {

                // vytvoříme lokální seznam se snapshotem první položky + kopiemi ostatních
                List<byte[]> frameBatch = new ArrayList<>();

                // 1) přidej aktuální snapshot deviceState jako první prvek
                synchronized (stateLock) {
                    frameBatch.add(deviceState.clone());
                }

                // 2) přidej kopie ostatních definovaných paketů (index 1..end)
                for (int i = 1; i < spontaneousPackets.size(); i++) {
                    frameBatch.add(spontaneousPackets.get(i).clone());
                }

                // 3) promíchej pořadí — snapshot je součástí mixu
                Collections.shuffle(frameBatch);

                // pošli prvních 5 (nebo méně)
                int toSendCount = Math.min(5, frameBatch.size());
                for (int i = 0; i < toSendCount; i++) {
                    byte[] packet = frameBatch.get(i);

                    int aaCount = 6 + random.nextInt(45);
                    byte[] prefix = new byte[aaCount];
                    Arrays.fill(prefix, (byte) 0xAA);
                    out.write(prefix);

                    Thread.sleep(1 + random.nextInt(4));

                    out.write(packet);
                    out.flush();

                    Thread.sleep(1000 + random.nextInt(1000));
                }

                Thread.sleep(random.nextInt(2000));
            }

        } catch (Exception ignored) {}
    }

    /* ---------------------------------------------------------------------- */
    /*  HELPER METHODS                                                      */
    /* ---------------------------------------------------------------------- */
    private static byte[] hexToBytes(String hex) {
        String[] tokens = hex.split("\\s+");
        byte[] out = new byte[tokens.length];
        for (int i = 0; i < tokens.length; i++)
            out[i] = (byte) Integer.parseInt(tokens[i], 16);
        return out;
    }

    private int indexOf(byte[] data, byte[] pattern) {
        outer:
        for (int i = 0; i <= data.length - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    public void stop() {
        running = false;
        for (Socket client : clients) {
            try { client.close(); } catch (Exception ignored) {}
        }
        clients.clear();
    }
}
