package com.joiner.ebus.service.crc;

import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.protherm.DataListener;
import com.joiner.ebus.communication.protherm.DataSender;
import com.joiner.ebus.communication.protherm.FrameReceivedEvent;
import com.joiner.ebus.communication.protherm.OperationalData;
import com.joiner.ebus.communication.protherm.RoomController;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DataCollector {

    @Autowired
    private DataSender dataSender;

    @Autowired
    private DataListener dataListener;

    private final ReentrantLock ebusLock = new ReentrantLock();

    @PostConstruct
    public void init() {
        dataSender.setLock(ebusLock);
        dataListener.setLock(ebusLock);
    }

    @Scheduled(fixedRate = 10000)
    public void sendData() {
        RoomController roomController = new RoomController();
        log.info("Client sending RoomController data: 30, 45.0, false, true");
        OperationalData operationalData = roomController.getOperationalData(30, 45.0, false, true);

        try {
            byte[] masterEcho = dataSender.sendFrame(operationalData);
            log.debug("Master echo:    {}", bytesToHex(masterEcho));
            log.debug("Slave response: {}", bytesToHex(operationalData.getSlaveData()));
            log.debug("Client adapted data -> Acknowledge: {}", roomController.getAcknowledge());
        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

    @Async
    @EventListener
    public void handleFrame(FrameReceivedEvent event) {
        log.info("Intercepted data: {} {}", bytesToHex(event.getAddress()), bytesToHex(event.getData()));
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

}
