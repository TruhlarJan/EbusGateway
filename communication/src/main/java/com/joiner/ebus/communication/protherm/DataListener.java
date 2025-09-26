package com.joiner.ebus.communication.protherm;

import java.io.InputStream;
import java.net.Socket;

import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataListener {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 3334;

    private StringBuilder stringBuilder = new StringBuilder();

    private Socket socket;
    private InputStream in;

    public void init() throws Exception {
        if (socket == null) {
            socket = new Socket(HOST, PORT);
            socket.setSoTimeout(2); // blokující čtení
            in = socket.getInputStream();
        }
    }
    
    public void poll() throws Exception {
        init();
        try {
            int b = in.read(); // čeká max 2 ms
            if (b != -1 && b != OperationalData.SYN) {
                stringBuilder.append(String.format("%02X", b));
            }
            if (stringBuilder.length() > 0 && b == OperationalData.SYN) {
                log.info("Data: {}", stringBuilder.toString());
                stringBuilder.setLength(0);
            }
        } catch (java.net.SocketTimeoutException e) {
            // žádný byte nepřišel, nevadí – prostě konec cyklu
        }
    }


    @PreDestroy
    public void shutdown() throws Exception {
        if (in != null) in.close();
        if (socket != null) socket.close();
    }
}
