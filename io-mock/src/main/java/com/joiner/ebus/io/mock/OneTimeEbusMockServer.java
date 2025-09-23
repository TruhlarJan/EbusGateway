package com.joiner.ebus.io.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OneTimeEbusMockServer implements Runnable {

    private final int port;
    private final Map<String, byte[]> addressToSlave = new HashMap<>();

    public OneTimeEbusMockServer(int port) {
        this.port = port;
        // Map of addresses (with spaces) -> corresponding slave data
        addressToSlave.put("10 08 B5 10", new byte[] { 0x00, 0x01, 0x01, (byte) 0x9A });
    }

    @Override
    public void run() {
        // This is a single-frame mock server for JUnit tests.
        // It accepts one client, reads one frame, responds, and exits.
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Mock eBUS server listening on port {}", port);

            try (Socket client = serverSocket.accept()) {
                log.info("Client {} connected.", client.getInetAddress());
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                // Slave sends the synchronization byte
                out.write((byte) 0xAA);
                out.flush();

                // Read the 4-byte address from master
                byte[] address = new byte[4];
                for (int i = 0; i < 4; i++) {
                    address[i] = (byte) in.read();
                }
                String addressHex = bytesToHexWithSpaces(address);
                log.info("Server received address: {}", addressHex);

                // Read the length byte
                int length = in.read();
                log.info("Server received length: {}", String.format("%02X", length));

                // Read master data frame including CRC
                byte[] masterCrcEnded = new byte[length + 1];
                for (int i = 0; i < masterCrcEnded.length; i++) {
                    masterCrcEnded[i] = (byte) in.read();
                }
                log.info("Server received master CRC ended: {}", bytesToHexWithSpaces(masterCrcEnded));

                // Select the slave response based on the received address
                byte[] slave = addressToSlave.get(addressHex);

                // Create the full response: address + length + master data + slave data
                byte[] response = new byte[address.length + 1 + masterCrcEnded.length + slave.length];
                System.arraycopy(address, 0, response, 0, address.length);
                response[address.length] = (byte) length;
                System.arraycopy(masterCrcEnded, 0, response, address.length + 1, masterCrcEnded.length);
                System.arraycopy(slave, 0, response, address.length + 1 + masterCrcEnded.length, slave.length);

                // Send the response back to the master
                out.write(response);
                out.flush();
                log.info("Server sent response ({} bytes): {}", response.length, bytesToHexWithSpaces(response));

                // Read the final ACK + SYN bytes from the master
                byte[] finalBytes = new byte[2];
                for (int i = 0; i < finalBytes.length; i++) {
                    finalBytes[i] = (byte) in.read();
                }
                log.info("Server received final bytes: {}", bytesToHexWithSpaces(finalBytes));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Utility method to convert a byte array to a hex string with spaces
    private static String bytesToHexWithSpaces(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}

