package com.joiner.ebus.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.joiner.ebus.api.DefaultApi;
import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.model.UnknownDto;
import com.joiner.ebus.service.BurnerControlUnitBlock0Service;
import com.joiner.ebus.service.BurnerControlUnitBlock1Service;
import com.joiner.ebus.service.BurnerControlUnitBlock2Service;
import com.joiner.ebus.service.RoomControlUnitService;
import com.joiner.ebus.service.UnknownService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/protherm")
@RequiredArgsConstructor
public class ProthermController implements DefaultApi {

    private final RoomControlUnitService roomControlUnitService;
    private final BurnerControlUnitBlock0Service burnerControlUnitBlock0Service;
    private final BurnerControlUnitBlock1Service burnerControlUnitBlock1Service;
    private final BurnerControlUnitBlock2Service burnerControlUnitBlock2Service;
    private final UnknownService unknownService;

    @Override
    public ResponseEntity<RoomControlUnitDto> readRoomControlUnit() {
        return ResponseEntity.ok(roomControlUnitService.getRoomControlUnitDto());
    }

    @Override
    public ResponseEntity<Void> updateRoomControlUnit(RoomControlUnitDto roomControlUnitDto) {
        roomControlUnitService.setRoomControlUnit(roomControlUnitDto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<BurnerControlUnitBlock0Dto> readBurnerControlUnitBlock0() {
        return ResponseEntity.ok(burnerControlUnitBlock0Service.getBurnerControlUnitBlock0Dto());
    }

    @Override
    public ResponseEntity<BurnerControlUnitBlock1Dto> readBurnerControlUnitBlock1() {
        return ResponseEntity.ok(burnerControlUnitBlock1Service.getBurnerControlUnitBlock1Dto());
    }

    @Override
    public ResponseEntity<BurnerControlUnitBlock2Dto> readBurnerControlUnitBlock2() {
        return ResponseEntity.ok(burnerControlUnitBlock2Service.getBurnerControlUnitBlock2Dto());
    }

    @Override
    public ResponseEntity<List<UnknownDto>> readUnknowns() {
        return ResponseEntity.ok(new ArrayList<>(unknownService.getUnknowns()));
    }
}
