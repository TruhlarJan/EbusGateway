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
    void handleFrame_addsConvertedUnknownToQueue() {
        TgUnknownData data = mock(TgUnknownData.class);
        UnknownDto dto = new UnknownDto().data("unknown");
        when(converter.convert(data)).thenReturn(dto);

        service.handleFrame(dataEventFactory.new TgUnknownDataReadyEvent(data));

        assertThat(service.getUnknowns()).containsExactly(dto);
        verify(converter).convert(data);
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
        assertThat(service.getUnknowns().getFirst().getData()).isEqualTo("unknown-1");
        assertThat(service.getUnknowns().getLast().getData()).isEqualTo("unknown-100");
    }
}