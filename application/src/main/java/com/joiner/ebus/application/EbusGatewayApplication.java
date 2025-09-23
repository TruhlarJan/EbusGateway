package com.joiner.ebus.application;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.joiner.ebus.communication.protherm.DataSender;
import com.joiner.ebus.communication.protherm.OperationalData;
import com.joiner.ebus.communication.protherm.RoomController;
import com.joiner.ebus.io.mock.MockEbusServer;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication(scanBasePackages = "com.joiner.ebus")
@Slf4j
public class EbusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbusGatewayApplication.class, args);
    }

    // CommandLineRunner se spustí hned po startu Spring Boot kontextu
    @Bean
    public CommandLineRunner run() {
        return args -> {
            
            // MOCK
            new Thread(new MockEbusServer(DataSender.PORT)).start();
            Thread.sleep(100);
            //
            
            log.info("Client is going to sent RoomController data: 30, 45.0, false, true");

            DataSender sender = new DataSender();
            RoomController roomController = new RoomController();
            OperationalData operationalData = roomController.getOperationalData(30, 45.0, false, true);
            byte[] masterEcho = sender.sendFrame(operationalData);

            log.debug("Master echo:    {}", bytesToHex(masterEcho));
            log.debug("Slave response: {}", bytesToHex(operationalData.getSlaveData()));
            log.info("Client adapted data -> Acknowladge: {}", roomController.getAcknowledge());

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

