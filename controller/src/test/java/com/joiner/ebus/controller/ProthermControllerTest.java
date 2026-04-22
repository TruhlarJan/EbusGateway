package com.joiner.ebus.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayDeque;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.model.FiringAutomatDto;
import com.joiner.ebus.model.HeaterControllerDto;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.model.UnknownDto;
import com.joiner.ebus.service.BurnerControlUnitBlock0Service;
import com.joiner.ebus.service.BurnerControlUnitBlock1Service;
import com.joiner.ebus.service.BurnerControlUnitBlock2Service;
import com.joiner.ebus.service.FiringAutomatService;
import com.joiner.ebus.service.HeaterControllerService;
import com.joiner.ebus.service.RoomControlUnitService;
import com.joiner.ebus.service.UnknownService;

class ProthermControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private RoomControlUnitService roomControlUnitService;

    private BurnerControlUnitBlock0Service burnerControlUnitBlock0Service;

    private BurnerControlUnitBlock1Service burnerControlUnitBlock1Service;

    private BurnerControlUnitBlock2Service burnerControlUnitBlock2Service;

    private HeaterControllerService heaterControllerService;

    private FiringAutomatService firingAutomatService;

    private UnknownService unknownService;

    @BeforeEach
    void setUp() {
        roomControlUnitService = mock(RoomControlUnitService.class);
        burnerControlUnitBlock0Service = mock(BurnerControlUnitBlock0Service.class);
        burnerControlUnitBlock1Service = mock(BurnerControlUnitBlock1Service.class);
        burnerControlUnitBlock2Service = mock(BurnerControlUnitBlock2Service.class);
        heaterControllerService = mock(HeaterControllerService.class);
        firingAutomatService = mock(FiringAutomatService.class);
        unknownService = mock(UnknownService.class);

        objectMapper = new ObjectMapper().findAndRegisterModules();

        ProthermController controller = new ProthermController(
                roomControlUnitService,
                burnerControlUnitBlock0Service,
                burnerControlUnitBlock1Service,
                burnerControlUnitBlock2Service,
                heaterControllerService,
                firingAutomatService,
                unknownService);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void readRoomControlUnit_returnsDtoJson() throws Exception {
        RoomControlUnitDto dto = new RoomControlUnitDto().data("room-control");
        when(roomControlUnitService.getRoomControlUnitDto()).thenReturn(dto);

        mockMvc.perform(get("/protherm/roomControlUnit"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("room-control"));

        verify(roomControlUnitService).getRoomControlUnitDto();
    }

    @Test
    void updateRoomControlUnit_readsJsonBodyAndDelegatesToService() throws Exception {
        RoomControlUnitDto dto = new RoomControlUnitDto().data("updated-room-control");

        mockMvc.perform(put("/protherm/roomControlUnit")
                .contentType(APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        ArgumentCaptor<RoomControlUnitDto> captor = ArgumentCaptor.forClass(RoomControlUnitDto.class);
        verify(roomControlUnitService).setRoomControlUnit(captor.capture());
        assertEquals("updated-room-control", captor.getValue().getData());
    }

    @Test
    void readBurnerControlUnitBlock0_returnsDtoJson() throws Exception {
        BurnerControlUnitBlock0Dto dto = new BurnerControlUnitBlock0Dto().data("block0");
        when(burnerControlUnitBlock0Service.getBurnerControlUnitBlock0Dto()).thenReturn(dto);

        mockMvc.perform(get("/protherm/burnerControlUnits/block0"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("block0"));

        verify(burnerControlUnitBlock0Service).getBurnerControlUnitBlock0Dto();
    }

    @Test
    void readBurnerControlUnitBlock1_returnsDtoJson() throws Exception {
        BurnerControlUnitBlock1Dto dto = new BurnerControlUnitBlock1Dto().data("block1");
        when(burnerControlUnitBlock1Service.getBurnerControlUnitBlock1Dto()).thenReturn(dto);

        mockMvc.perform(get("/protherm/burnerControlUnits/block1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("block1"));

        verify(burnerControlUnitBlock1Service).getBurnerControlUnitBlock1Dto();
    }

    @Test
    void readBurnerControlUnitBlock2_returnsDtoJson() throws Exception {
        BurnerControlUnitBlock2Dto dto = new BurnerControlUnitBlock2Dto().data("block2");
        when(burnerControlUnitBlock2Service.getBurnerControlUnitBlock2Dto()).thenReturn(dto);

        mockMvc.perform(get("/protherm/burnerControlUnits/block2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("block2"));

        verify(burnerControlUnitBlock2Service).getBurnerControlUnitBlock2Dto();
    }

    @Test
    void readHeaterController_returnsDtoJson() throws Exception {
        HeaterControllerDto dto = new HeaterControllerDto().data("heater-controller");
        when(heaterControllerService.getHeaterControllerDto()).thenReturn(dto);

        mockMvc.perform(get("/protherm/heaterController"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("heater-controller"));

        verify(heaterControllerService).getHeaterControllerDto();
    }

    @Test
    void readFiringAutomat_returnsDtoJson() throws Exception {
        FiringAutomatDto dto = new FiringAutomatDto().data("firing-automat");
        when(firingAutomatService.getFiringAutomatDto()).thenReturn(dto);

        mockMvc.perform(get("/protherm/firingAutomat"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value("firing-automat"));

        verify(firingAutomatService).getFiringAutomatDto();
    }

    @Test
    void readUnknowns_returnsJsonArray() throws Exception {
        UnknownDto first = new UnknownDto().data("first");
        UnknownDto second = new UnknownDto().data("second");
        ArrayDeque<UnknownDto> unknowns = new ArrayDeque<>();
        unknowns.add(first);
        unknowns.add(second);
        when(unknownService.getUnknowns()).thenReturn(unknowns);

        mockMvc.perform(get("/protherm/unknowns"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].data").value("first"))
                .andExpect(jsonPath("$[1].data").value("second"));

        verify(unknownService).getUnknowns();
    }
}