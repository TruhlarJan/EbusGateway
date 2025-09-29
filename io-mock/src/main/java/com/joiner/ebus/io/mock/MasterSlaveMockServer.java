package com.joiner.ebus.io.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.springframework.stereotype.Service;

import com.joiner.ebus.io.mock.MockContainer.MockData;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MasterSlaveMockServer {

    private static final int PORT = 3333;
    private volatile boolean running = true;
    private MockContainer mockContainer;

    public MasterSlaveMockServer(MockContainer mockContainer) {
        this.mockContainer = mockContainer;
        mockContainer.setData(17629583509760L, 15, new byte[]{0x00, 0x01, 0x01, (byte) 0x9A});
        mockContainer.setData(17629583573248L, 7, new byte[]{0x00, 0x08, 0x50, 0x02, 0x0C, 0x00, 0x1F, 0x10, 0x00, (byte) 0x80, (byte) 0x07});
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
                if (running) log.error("Server error", e);
            }
        }, "MasterSlaveMockServerThread").start();
    }

    private void handleTransaction(Socket client) {
        try (InputStream in = client.getInputStream();
             OutputStream out = client.getOutputStream()) {

            out.write(0xAA);
            out.flush();

            byte[] address = in.readNBytes(6);
            String addrHex = bytesToHex(address);
            log.info("Master–slave transaction started with master bytes: {}", addrHex);

            MockData mockConteiner = mockContainer.getData(getKey(address));
            byte[] master = in.readNBytes(mockConteiner.getLength() - 6);
            byte[] slave = mockConteiner.getSlave();

            byte[] response = new byte[address.length + master.length + slave.length];
            System.arraycopy(address, 0, response, 0, address.length);
            System.arraycopy(master, 0, response, address.length, master.length);
            System.arraycopy(slave, 0, response, address.length + master.length, slave.length);

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
    
    
    private long getKey(byte[] byteArray) {
        long key = 0;
        for (int i = 0; i < 6; i++) {
            key = (key << 8) | (byteArray[i] & 0xFFL);
        }
        return key;
    }

}
