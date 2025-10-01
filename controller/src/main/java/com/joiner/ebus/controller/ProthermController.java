package com.joiner.ebus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joiner.ebus.service.BurnerControlService;
import com.joiner.ebus.service.dto.BurnerControlUnitsDto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock00Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock01Dto;
import com.joiner.ebus.service.dto.BurnerControlUnitBlock02Dto;

@RestController
@RequestMapping("/protherm")
public class ProthermController {

    @Autowired
    private BurnerControlService burnerControlService;
    
    @GetMapping("/units")
    public ResponseEntity<BurnerControlUnitsDto> readBurnerControlUnit() {
        return new ResponseEntity<>(burnerControlService.getBurnerControlUnits(), HttpStatus.OK);
    }

    @GetMapping("/units/0")
    public ResponseEntity<BurnerControlUnitBlock00Dto> readBurnerControlUnitBlock00() {
        return new ResponseEntity<>(burnerControlService.getBurnerControlUnitBlock00Dto(), HttpStatus.OK);
    }

    @GetMapping("/units/1")
    public ResponseEntity<BurnerControlUnitBlock01Dto> readBurnerControlUnitBlock01() {
        return new ResponseEntity<>(burnerControlService.getBurnerControlUnitBlock01Dto(), HttpStatus.OK);
    }

    @GetMapping("/units/2")
    public ResponseEntity<BurnerControlUnitBlock02Dto> readBurnerControlUnitBlock02() {
        return new ResponseEntity<>(burnerControlService.getBurnerControlUnitBlock02Dto(), HttpStatus.OK);
    }

}
