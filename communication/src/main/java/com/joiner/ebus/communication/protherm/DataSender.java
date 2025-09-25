package com.joiner.ebus.communication.protherm;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.springframework.stereotype.Component;

@Component
public class DataSender {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 3333;

    public byte[] sendFrame(OperationalData data) throws Exception {
        try (Socket socket = new Socket(HOST, PORT)) {
            socket.setSoTimeout(2000); // timeout 2s

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            // -------------------------------
            // čekáme na první SYN (0xAA) - ignorujeme přebytečné SYNy
            // -------------------------------
            int readByte;
            do {
                readByte = in.read();
                if (readByte == -1) {
                    throw new RuntimeException("Connection closed before receiving SYN");
                }
            } while (readByte != OperationalData.SYN);

            // -------------------------------
            // pošleme master rámec po bytech
            // -------------------------------
            byte[] masterFrame = data.getMasterCrcEndedData();
            for (byte b : masterFrame) {
                out.write(b & 0xFF);
                out.flush();
            }

            // -------------------------------
            // načteme master echo po bytech
            // -------------------------------
            byte[] masterEcho = new byte[masterFrame.length];
            int read = 0;
            while (read < masterEcho.length) {
                int r = in.read(masterEcho, read, masterEcho.length - read);
                if (r == -1) {
                    throw new RuntimeException("Connection closed while reading master echo");
                }
                read += r;
            }

            // -------------------------------
            // načteme slave odpověď přesně podle velikosti
            // -------------------------------
            byte[] slaveResponse = new byte[data.getSlaveSize()];
            read = 0;
            while (read < slaveResponse.length) {
                int r = in.read(slaveResponse, read, slaveResponse.length - read);
                if (r == -1) {
                    throw new RuntimeException("Connection closed before receiving slave response");
                }
                read += r;
            }

            // -------------------------------
            // pošleme ACK (a SYN) po bytech
            // -------------------------------
            byte[] ack = data.getMasterFinalData();
            for (byte b : ack) {
                out.write(b & 0xFF);
                out.flush();
            }

            data.setSlaveData(slaveResponse);
            return masterEcho;
        }
    }

}
