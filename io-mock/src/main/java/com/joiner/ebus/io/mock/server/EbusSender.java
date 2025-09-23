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

    private final EbusProperties props;
    private final EbusServer server;
    private final Random random = new Random();
    private int packetIndex = 0;
    private final List<byte[]> packetBytes = new ArrayList<>();

    public EbusSender(EbusProperties properties, EbusServer server) {
        this.props = properties;
        this.server = server;

        // převod hex stringů na byte[]
        for (String hexPacket : props.getPackets()) {
            String[] tokens = hexPacket.split("\\s+");
            byte[] bytes = new byte[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                bytes[i] = (byte) Integer.parseInt(tokens[i], 16);
            }
            packetBytes.add(bytes);
        }
    }

    @Scheduled(fixedRateString = "${ebus.interval}")
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

        // poslat všem klientům
        for (Socket client : server.getClients()) {
            try {
                OutputStream out = client.getOutputStream();
                out.write(frame);
                out.flush();
            } catch (Exception e) {
                server.getClients().remove(client); // odpojený klient
                log.info("Client disconnected during send: " + client.getInetAddress());
            }
        }

        // log pouze AA prefix a samotný paket
        log.info("Sent AA: {} | Packet: {}", String.format("%-2d", aaCount), toHex(packet));
    }

    private String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
