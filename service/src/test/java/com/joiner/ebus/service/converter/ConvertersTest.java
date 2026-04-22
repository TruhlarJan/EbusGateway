package com.joiner.ebus.service.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.protherm.Tg0315B513Data;
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
import com.joiner.ebus.communication.protherm.TgUnknownData;
import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock1Dto;
import com.joiner.ebus.model.BurnerControlUnitBlock2Dto;
import com.joiner.ebus.model.FiringAutomatDto;
import com.joiner.ebus.model.HeaterControllerDto;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.model.UnknownDto;

class ConvertersTest {

    @Test
    void byteArrayToStringConverter_formatsUppercaseHexBytes() {
        ByteArrayToStringConverter converter = new ByteArrayToStringConverter();

        String converted = converter.convert(new byte[] {0x00, 0x0A, 0x7F});

        assertThat(converted).isEqualTo("00 0A 7F");
    }

    @Test
    void roomControlUnitDtoToTg1008B510DataConverter_packsTemperaturesAndBlockingFlags() {
        RoomControlUnitDtoToTg1008B510DataConverter converter = new RoomControlUnitDtoToTg1008B510DataConverter();
        RoomControlUnitDto source = new RoomControlUnitDto()
                .leadWaterTargetTemperature(48.4)
                .serviceWaterTargetTemperature(45.2)
                .leadWaterHeatingBlocked(1)
                .serviceWaterHeatingBlocked(1);

        Tg1008B510Data converted = converter.convert(source);

        assertThat(converted).isNotNull();
        assertThat(converted.getMasterData()[Tg1008B510Data.M8_INDEX]).isEqualTo((byte) 97);
        assertThat(converted.getMasterData()[Tg1008B510Data.M9_INDEX]).isEqualTo((byte) 90);
        assertThat(converted.getMasterData()[Tg1008B510Data.M12_INDEX]).isEqualTo((byte) 5);
    }

    @Test
    void tg1008B510DataToRoomControlUnitDtoConverter_mapsTelegramToDto() {
        ConversionService conversionService = mock(ConversionService.class);
        Tg1008B510DataToRoomControlUnitDtoConverter converter = withConversionService(new Tg1008B510DataToRoomControlUnitDtoConverter(), conversionService);
        Tg1008B510Data source = new Tg1008B510Data(97, 90, 5);

        when(conversionService.convert(source.getMasterData(), String.class)).thenReturn("room-control");

        RoomControlUnitDto converted = converter.convert(source);

        assertThat(converted.getData()).isEqualTo("room-control");
        assertThat(converted.getDateTime()).isNotNull();
        assertThat(converted.getLeadWaterTargetTemperature()).isEqualTo(48.5);
        assertThat(converted.getServiceWaterTargetTemperature()).isEqualTo(45.0);
        assertThat(converted.getLeadWaterHeatingBlocked()).isEqualTo(1);
        assertThat(converted.getServiceWaterHeatingBlocked()).isEqualTo(1);
    }

    @Test
    void tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter_mapsTelegramToDto() {
        ConversionService conversionService = mock(ConversionService.class);
        Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter converter = withConversionService(new Tg1008B5110100DataToBurnerControlUnitBlock0DtoConverter(), conversionService);
        Tg1008B5110100Data source = new Tg1008B5110100Data();
        byte[] slaveData = new byte[11];
        slaveData[Tg1008B5110100Data.GT2_INDEX] = 2;
        slaveData[Tg1008B5110100Data.GT1_INDEX] = 1;
        slaveData[Tg1008B5110100Data.WP_INDEX] = 15;
        slaveData[Tg1008B5110100Data.BP_INDEX] = 40;
        source.setSlaveData(slaveData);

        when(conversionService.convert(slaveData, String.class)).thenReturn("block0");

        BurnerControlUnitBlock0Dto converted = converter.convert(source);

        assertThat(converted.getData()).isEqualTo("block0");
        assertThat(converted.getDateTime()).isNotNull();
        assertThat(converted.getPrimaryTemperature()).isCloseTo(25.7, within(0.0001));
        assertThat(converted.getWaterPressure()).isEqualTo(1.5);
        assertThat(converted.getFlameBurningPower()).isEqualTo(20.0);
    }

    @Test
    void tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter_mapsTelegramToDto() {
        ConversionService conversionService = mock(ConversionService.class);
        Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter converter = withConversionService(new Tg1008B5110101DataToBurnerControlUnitBlock1DtoConverter(), conversionService);
        Tg1008B5110101Data source = new Tg1008B5110101Data();
        byte[] slaveData = new byte[12];
        slaveData[Tg1008B5110101Data.VT_INDEX] = 50;
        slaveData[Tg1008B5110101Data.NT_INDEX] = 60;
        slaveData[Tg1008B5110101Data.ST_INDEX] = 70;
        slaveData[Tg1008B5110101Data.VV_INDEX] = 0b0000_0101;
        source.setSlaveData(slaveData);

        when(conversionService.convert(slaveData, String.class)).thenReturn("block1");

        BurnerControlUnitBlock1Dto converted = converter.convert(source);

        assertThat(converted.getData()).isEqualTo("block1");
        assertThat(converted.getDateTime()).isNotNull();
        assertThat(converted.getLeadWaterTemperature()).isEqualTo(25.0);
        assertThat(converted.getReturnWaterTemperature()).isEqualTo(30.0);
        assertThat(converted.getServiceWaterTemperature()).isEqualTo(35.0);
        assertThat(converted.getHeatingOn()).isEqualTo(1);
        assertThat(converted.getServiceWaterOn()).isEqualTo(1);
    }

    @Test
    void tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter_mapsTelegramToDto() {
        ConversionService conversionService = mock(ConversionService.class);
        Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter converter = withConversionService(new Tg1008B5110102DataToBurnerControlUnitBlock2DtoConverter(), conversionService);
        Tg1008B5110102Data source = new Tg1008B5110102Data();
        byte[] slaveData = new byte[8];
        slaveData[Tg1008B5110102Data.VV_INDEX] = 0b0000_0011;
        source.setSlaveData(slaveData);

        when(conversionService.convert(slaveData, String.class)).thenReturn("block2");

        BurnerControlUnitBlock2Dto converted = converter.convert(source);

        assertThat(converted.getData()).isEqualTo("block2");
        assertThat(converted.getDateTime()).isNotNull();
        assertThat(converted.getHeatingEnabled()).isEqualTo(1);
        assertThat(converted.getServiceWaterEnabled()).isEqualTo(1);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 0",
            "100, 1",
            "85, -1"
    })
    void tg0315B513DataToFiringAutomatDtoConverter_mapsPumpState(int rawValue, int expectedState) {
        ConversionService conversionService = mock(ConversionService.class);
        Tg0315B513DataToFiringAutomatDtoConverter converter = withConversionService(new Tg0315B513DataToFiringAutomatDtoConverter(), conversionService);
        byte[] masterData = new byte[Tg0315B513Data.YY_INDEX + 1];
        masterData[Tg0315B513Data.YY_INDEX] = (byte) rawValue;
        Tg0315B513Data source = new Tg0315B513Data(masterData);

        when(conversionService.convert(masterData, String.class)).thenReturn("firing-automat");

        FiringAutomatDto converted = converter.convert(source);

        assertThat(converted.getData()).isEqualTo("firing-automat");
        assertThat(converted.getDateTime()).isNotNull();
        assertThat(converted.getInternalPump()).isEqualTo(expectedState);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 1",
            "100, 2",
            "254, 0",
            "85, -1"
    })
    void tg0364B512DataToHeaterControllerDtoConverter_mapsPumpState(int rawValue, int expectedState) {
        ConversionService conversionService = mock(ConversionService.class);
        Tg0364B512DataToHeaterControllerDtoConverter converter = withConversionService(new Tg0364B512DataToHeaterControllerDtoConverter(), conversionService);
        byte[] masterData = new byte[Tg0364B512Data.YY_INDEX + 1];
        masterData[Tg0364B512Data.YY_INDEX] = (byte) rawValue;
        Tg0364B512Data source = new Tg0364B512Data(masterData);

        when(conversionService.convert(masterData, String.class)).thenReturn("heater-controller");

        HeaterControllerDto converted = converter.convert(source);

        assertThat(converted.getData()).isEqualTo("heater-controller");
        assertThat(converted.getDateTime()).isNotNull();
        assertThat(converted.getWaterCirculatingPump()).isEqualTo(expectedState);
    }

    @Test
    void tgUnknownDataToUnknownDtoConverter_mapsRawMessageToDto() {
        ConversionService conversionService = mock(ConversionService.class);
        TgUnknownDataToUnknownDtoConverter converter = withConversionService(new TgUnknownDataToUnknownDtoConverter(), conversionService);
        byte[] masterData = new byte[] {0x01, 0x23, 0x45};
        TgUnknownData source = new TgUnknownData(masterData);

        when(conversionService.convert(masterData, String.class)).thenReturn("unknown");

        UnknownDto converted = converter.convert(source);

        assertThat(converted.getData()).isEqualTo("unknown");
        assertThat(converted.getDateTime()).isNotNull();
    }

    private static <T> T withConversionService(T converter, ConversionService conversionService) {
        ReflectionTestUtils.setField(converter, "conversionService", conversionService);
        return converter;
    }
}