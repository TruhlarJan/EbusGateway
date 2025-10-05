package com.joiner.ebus.communication.link;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterSlaveData;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class EbusMasterSlaveLink {

    @Value("${adapter.host:172.20.10.3}")
    private String host;

    @Value("${adapter.port.raw:3333}")
    private int port;
    

    public void sendFrame(MasterSlaveData masterSlaveData) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            socket.setSoTimeout(2000);
            
            try (OutputStream out = socket.getOutputStream();
                InputStream in = socket.getInputStream()) {
                // -------------------------------
                // čekáme na první SYN (0xAA)
                // -------------------------------
                int readByte;
                do {
                    readByte = in.read();
                    if (readByte == -1) {
                        throw new RuntimeException("Connection closed before receiving SYN");
                    }
                } while (readByte != MasterSlaveData.SYN);

                
                // -------------------------------
                // pošleme master rámec po bytech
                // -------------------------------
                out.write(masterSlaveData.getMasterData());
                out.flush();
            }
        } catch (Exception e) {
            log.warn("sendFrame() failed: {}", e.getMessage(), e);
        }
        
    }

}
