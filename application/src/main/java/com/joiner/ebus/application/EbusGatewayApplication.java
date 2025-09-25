package com.joiner.ebus.application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.joiner.ebus.communication.protherm.DataSender;
import com.joiner.ebus.communication.protherm.OperationalData;
import com.joiner.ebus.communication.protherm.RoomController;
import com.joiner.ebus.io.mock.UnifiedEbusMockServer;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = "com.joiner.ebus")
@EnableScheduling
@Slf4j
public class EbusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbusGatewayApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UnifiedEbusMockServer unifiedEbusMock) {
        return args -> {
            log.info("Client sending RoomController data: 30, 45.0, false, true");

            DataSender sender = new DataSender();
            RoomController roomController = new RoomController();
            OperationalData operationalData = roomController.getOperationalData(30, 45.0, false, true);

            byte[] masterEcho = sender.sendFrame(operationalData);

            log.debug("Master echo:    {}", bytesToHex(masterEcho));
            log.debug("Slave response: {}", bytesToHex(operationalData.getSlaveData()));
            log.info("Client adapted data -> Acknowledge: {}", roomController.getAcknowledge());
        };
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
