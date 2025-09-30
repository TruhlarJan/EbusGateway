package com.joiner.ebus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joiner.ebus.service.BurnerControlService;
import com.joiner.ebus.service.dto.BurnerControlUnit;

@RestController
@RequestMapping("/protherm")
public class ProthermController {

    @Autowired
    private BurnerControlService burnerControlService;
    
    @GetMapping
    public ResponseEntity<BurnerControlUnit> readBurnerControlUnit() {
        BurnerControlUnit burnerControlUnitBlock01 = burnerControlService.getBurnerControlUnit();
        return new ResponseEntity<>(burnerControlUnitBlock01, HttpStatus.OK);
    }
    
}
