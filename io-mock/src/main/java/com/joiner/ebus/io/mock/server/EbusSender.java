package com.joiner.ebus.io.mock.server;

import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.net.Socket;

@Service
@Slf4j
public class EbusSender {

    private final EbusServer server;
    private final Random random = new Random();
    private int packetIndex = 0;
    private final List<byte[]> packetBytes = new ArrayList<>();

    public EbusSender(EbusServer server) {
        this.server = server;
        String[] packets = {
                "03 15 B5 13 03 06 00 00 0E",
                "03 15 B5 13 03 06 64 00 63",
                "03 64 B5 12 02 02 00 66",
                "03 64 b5 12 02 02 64 02",
                "03 64 b5 12 02 02 fe 98"};
                
        // převod hex stringů na byte[]
        for (String hexPacket : packets) {
            String[] tokens = hexPacket.split("\\s+");
            byte[] bytes = new byte[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                bytes[i] = (byte) Integer.parseInt(tokens[i], 16);
            }
            packetBytes.add(bytes);
        }
    }

    @Scheduled(fixedRate = 3000)
    public void sendPacket() {
        if (server.getClients().isEmpty()) return;

        // náhodný počet 0xAA před paketem
        int aaCount = 5 + random.nextInt(26);
        byte[] aaPrefix = new byte[aaCount];
        for (int i = 0; i < aaCount; i++) aaPrefix[i] = (byte) 0xAA;

        // vybraný paket (cyklicky)
        byte[] packet = packetBytes.get(packetIndex);
        packetIndex = (packetIndex + 1) % packetBytes.size();

        // spojení rámce
        byte[] frame = new byte[aaCount + packet.length];
        System.arraycopy(aaPrefix, 0, frame, 0, aaCount);
        System.arraycopy(packet, 0, frame, aaCount, packet.length);

        // poslat všem klientům po byte s prodlevou 1ms
        for (Socket client : server.getClients()) {
            try {
                OutputStream out = client.getOutputStream();
                for (byte b : frame) {
                    out.write(b);
                    out.flush();
                    // eBUS má rychlost 2400 Bd → jeden byte (10 bitů: start, 8 dat, stop) zabere cca 4,17 ms.
                    Thread.sleep(4, 170_000); // 4 ms + 170 000 ns = 4,170 ms
                }
            } catch (Exception e) {
                server.getClients().remove(client); // odpojený klient
                log.info("Client disconnected during send: " + client.getInetAddress());
            }
        }

        // log počet AA + samotný paket
        log.debug("Sent {}x AA + {}", String.format("%-2d", aaCount), toHex(packet));
    }

    private String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
