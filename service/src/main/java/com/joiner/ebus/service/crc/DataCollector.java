package com.joiner.ebus.service.crc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.protherm.DataSender;
import com.joiner.ebus.communication.protherm.OperationalData;
import com.joiner.ebus.communication.protherm.RoomController;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCollector {

    @Autowired
    private DataSender dataSender;

    @Scheduled(fixedRate = 10000)
    private void sendData() {
        RoomController roomController = new RoomController();
        log.info("Client sending RoomController data: 30, 45.0, false, true");
        OperationalData operationalData = roomController.getOperationalData(30, 45.0, false, true);

        try {
            byte[] masterEcho = dataSender.sendFrame(operationalData);
            log.debug("Master echo:    {}", bytesToHex(masterEcho));
            log.debug("Slave response: {}", bytesToHex(operationalData.getSlaveData()));
            log.info("Client adapted data -> Acknowledge: {}", roomController.getAcknowledge());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}
