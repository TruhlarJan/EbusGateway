package com.joiner.ebus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.BurnerControlService;
import com.joiner.ebus.service.BurnerControlUnitBlock0Service;
import com.joiner.ebus.service.BurnerControlUnitBlock1Service;
import com.joiner.ebus.service.BurnerControlUnitBlock2Service;
import com.joiner.ebus.service.RoomControlUnitService;
import com.joiner.ebus.service.dto.BurnerControlUnitsDto;

@RestController
@RequestMapping("/protherm")
public class ProthermController {

    @Autowired
    private BurnerControlUnitBlock0Service burnerControlUnitBlock0Service;

    @Autowired
    private BurnerControlUnitBlock1Service burnerControlUnitBlock1Service;
    
    @Autowired
    private BurnerControlUnitBlock2Service burnerControlUnitBlock2Service;
    
    @Autowired
    private BurnerControlService burnerControlService;
    
    @Autowired
    private RoomControlUnitService roomControlService;
    
    @GetMapping("/units")
    public ResponseEntity<BurnerControlUnitsDto> readBurnerControlUnit() {
        return new ResponseEntity<>(burnerControlService.getBurnerControlUnits(), HttpStatus.OK);
    }

    @PutMapping("/units")
    public ResponseEntity<?> updateRoomControlUnit(@RequestBody RoomControlUnitDto roomControlUnitDto) {
        roomControlService.setRoomControlUnit(roomControlUnitDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/burnerControlUnits/block0")
    public ResponseEntity<BurnerControlUnitBlock0Dto> readBurnerControlUnitBlock00() {
        return new ResponseEntity<>(burnerControlUnitBlock0Service.getBurnerControlUnitBlock0Dto(), HttpStatus.OK);
    }

    @GetMapping("/burnerControlUnits/block1")
    public ResponseEntity<BurnerControlUnitBlock1Dto> readBurnerControlUnitBlock1() {
        return new ResponseEntity<>(burnerControlUnitBlock1Service.getBurnerControlUnitBlock1Dto(), HttpStatus.OK);
    }

    @GetMapping("/burnerControlUnits/block2")
    public ResponseEntity<BurnerControlUnitBlock2Dto> readBurnerControlUnitBlock2() {
        return new ResponseEntity<>(burnerControlUnitBlock2Service.getBurnerControlUnitBlock2Dto(), HttpStatus.OK);
    }
    
}
