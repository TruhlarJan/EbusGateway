package com.joiner.ebus.protherm;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MockEbusServer implements Runnable {

    private final int port;

    public MockEbusServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Mock eBUS server listening on port {}", port);

            try (Socket client = serverSocket.accept()) {
                log.info("Client {} connected.", client.getInetAddress());
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();

                // slave posílá synchro byte
                byte[] syncByte = {(byte) 0xAA};
                out.write(syncByte);
                out.flush();
                
                // přečteme rámec od mastera
                byte[] buffer = new byte[1024];
                int length = in.read(buffer);
                
                StringBuilder mHex = new StringBuilder();
                for (int i = 0; i < length; i++) {
                    mHex.append(String.format("%02X ", buffer[i]));
                }
                log.info("Server received ({} bytes): {}", length, mHex.toString().trim());

                if (length > 0 ) {
                    
                    // pošleme echo a slave odpověď (ACK, NN, ZZ, CRC)
                    byte[] response = { 
                            0x10, 0x08, (byte) 0xB5, 0x10, 0x09, 0x00, 0x00, 0x3E, 0x5A, (byte) 0xFF, (byte) 0xFF, 0x01, (byte) 0xFF, 0x00, 0x1D,
                            0x00, 0x01, 0x01, (byte) 0x9A };
                    out.write(response);
                    out.flush();

                    StringBuilder sHex = new StringBuilder();
                    for (byte b : response) {
                        sHex.append(String.format("%02X ", b));
                    }
                    log.info("Server sent data ({} bytes): {}", response.length, sHex.toString().trim());
                    
                    // přečteme finální ACK+SYN od mastera
                    int ackByte = in.read();
                    int synByte = in.read();
                    log.info(String.format("Server received final byte: %02X %02X", ackByte, synByte));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

