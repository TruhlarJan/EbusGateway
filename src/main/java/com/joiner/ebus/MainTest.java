package com.joiner.ebus;

import com.joiner.ebus.protherm.DataSender;
import com.joiner.ebus.protherm.OperationalData;
import com.joiner.ebus.protherm.RoomController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainTest {

    public static void main(String[] args) throws Exception {
        log.info("Client is going to sent RoomController data: 0, 48.0, false, true");

        DataSender sender = new DataSender();
        RoomController roomController = new RoomController();
        OperationalData operationalData = roomController.getOperationalData(30, 50, false, false);
        byte[] masterEcho = sender.sendFrame(operationalData);

        log.debug("Master echo:    {}", bytesToHex(masterEcho));
        log.debug("Slave response: {}", bytesToHex(operationalData.getSlaveData()));
        log.info("Client adapted data -> Acknowladge: {}", roomController.getAcknowledge());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }
}

