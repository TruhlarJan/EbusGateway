package com.joiner.ebus.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.joiner.ebus.communication.link.DataEventFactory;
import com.joiner.ebus.communication.protherm.TgUnknownData;
import com.joiner.ebus.model.UnknownDto;
import com.joiner.ebus.service.converter.TgUnknownDataToUnknownDtoConverter;

class UnknownServiceTest {

    private final DataEventFactory dataEventFactory = new DataEventFactory();

    private TgUnknownDataToUnknownDtoConverter converter;

    private UnknownService service;

    @BeforeEach
    void setUp() {
        converter = mock(TgUnknownDataToUnknownDtoConverter.class);

        service = new UnknownService();
        ReflectionTestUtils.setField(service, "converter", converter);
    }

    @Test
    void handleFrame_addsConvertedUnknown() {
        TgUnknownData data = mock(TgUnknownData.class);
        UnknownDto dto = new UnknownDto().data("unknown");

        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new TgUnknownDataReadyEvent(data));

        assertThat(service.getUnknowns()).containsKey("unknown");

        assertThat(service.getUnknowns().get("unknown")).isSameAs(dto);

        assertThat(dto.getDateTimes()).hasSize(1);

        verify(converter).convert(data);
    }

    @Test
    void handleFrame_whenSameTelegramIsReceived_addsAnotherDateTime() {
        TgUnknownData data1 = mock(TgUnknownData.class, "unknown-data-1");
        TgUnknownData data2 = mock(TgUnknownData.class, "unknown-data-2");

        UnknownDto dto1 = new UnknownDto().data("unknown");

        UnknownDto dto2 = new UnknownDto().data("unknown");

        when(converter.convert(data1)).thenReturn(dto1);
        when(converter.convert(data2)).thenReturn(dto2);

        service.handleFrame(dataEventFactory.new TgUnknownDataReadyEvent(data1));
        service.handleFrame(dataEventFactory.new TgUnknownDataReadyEvent(data2));

        assertThat(service.getUnknowns()).hasSize(1).containsKey("unknown");

        UnknownDto stored = service.getUnknowns().get("unknown");

        assertThat(stored).isSameAs(dto1);

        assertThat(stored.getDateTimes()).hasSize(2).allMatch(dateTime -> dateTime != null);

        verify(converter).convert(data1);
        verify(converter).convert(data2);
    }

    @Test
    void handleFrame_whenCapacityIsExceeded_discardsOldestEntry() {
        for (int i = 0; i < 101; i++) {
            TgUnknownData data = mock(TgUnknownData.class, "unknown-data-" + i);
            UnknownDto dto = new UnknownDto().data("unknown-" + i);

            when(converter.convert(data)).thenReturn(dto);

            service.handleFrame(dataEventFactory.new TgUnknownDataReadyEvent(data));
        }

        assertThat(service.getUnknowns()).hasSize(100);

        assertThat(service.getUnknowns()).doesNotContainKey("unknown-0");

        assertThat(service.getUnknowns()).containsKey("unknown-1").containsKey("unknown-100");
    }

    @Test
    void handleFrame_whenDataIsNull_doesNotAddUnknown() {
        TgUnknownData data = mock(TgUnknownData.class);
        UnknownDto dto = new UnknownDto().data(null);

        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new TgUnknownDataReadyEvent(data));

        assertThat(service.getUnknowns()).isEmpty();

        verify(converter).convert(data);
    }

}
