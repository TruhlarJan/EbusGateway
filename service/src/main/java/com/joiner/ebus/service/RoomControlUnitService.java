
package com.joiner.ebus.service;

import java.beans.FeatureDescriptor;
import java.time.OffsetDateTime;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.joiner.ebus.communication.DataCollector;
import com.joiner.ebus.communication.link.DataEventFactory.Tg1008B510DataReadyEvent;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.model.RoomControlUnitDto;
import com.joiner.ebus.service.converter.RoomControlUnitDtoToTg1008B510DataConverter;
import com.joiner.ebus.service.converter.Tg1008B510DataToRoomControlUnitDtoConverter;
import com.joiner.ebus.service.event.RoomControlUnitMqttEvent;

import lombok.Getter;

/**
 * Service responsible for managing the state of the room control unit.
 *
 * <p>This service merges two asynchronous data sources:
 * <ul>
 *     <li><b>SET updates</b> - incoming user/system commands that define the authoritative state</li>
 *     <li><b>EBUS frames</b> - periodically received device state updates (delayed echo of previous commands)</li>
 * </ul>
 *
 * <p><b>Important consistency rule:</b>
 * SET operations always take precedence over EBUS frames.
 * An EBUS frame is applied only if it is newer than the last SET operation,
 * preventing outdated device feedback from overwriting recent user changes.
 *
 * <p>Thread safety is ensured via an internal lock protecting the shared DTO state.
 * AtomicLong is used to track the timestamp of the last SET operation.
 *
 * <p>Events are published only after a successful state change.
 */
@Service
public class RoomControlUnitService {

    private final Object lock = new Object();

    @Autowired
    private Tg1008B510DataToRoomControlUnitDtoConverter converter;

    @Autowired
    private RoomControlUnitDtoToTg1008B510DataConverter converter2;

    @Autowired
    private DataCollector dataCollector;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Getter
    private RoomControlUnitDto roomControlUnitDto = new RoomControlUnitDto();

    private final AtomicLong lastSetTime = new AtomicLong();

    /**
     * Handles incoming EBUS frames asynchronously.
     *
     * <p>Only applies updates if the frame is newer than the last SET operation.
     * Otherwise, the frame is ignored to prevent stale data from overwriting
     * the current state.
     *
     * @param event incoming EBUS data event
     */
    @Async
    @EventListener
    public void handleFrame(Tg1008B510DataReadyEvent event) {
        RoomControlUnitDto snapshot = null;
        synchronized (lock) {
            Tg1008B510Data data = event.getData();
            if (data.getDate().getTime() > lastSetTime.get()) {
                copyNonNullProperties(converter.convert(data), roomControlUnitDto);
                snapshot = roomControlUnitDto;
            }
        }
        if (snapshot != null) {
            eventPublisher.publishEvent(new RoomControlUnitMqttEvent(snapshot));
        }
    }

    /**
     * Applies a new SET update to the room control unit state.
     *
     * <p>This operation represents the authoritative source of truth.
     * It updates the internal state and marks the timestamp so that
     * older EBUS frames cannot overwrite it.
     *
     * @param roomControlUnitDtoStub partial update containing new values
     */
    public void setRoomControlUnit(RoomControlUnitDto roomControlUnitDtoStub) {
        RoomControlUnitDto snapshot;
        synchronized (lock) {
            lastSetTime.set(System.currentTimeMillis());
            roomControlUnitDtoStub.setDateTime(OffsetDateTime.now());
            copyNonNullProperties(roomControlUnitDtoStub, roomControlUnitDto);
            snapshot = roomControlUnitDto;
        }
        dataCollector.sendDataImmidiately(converter2.convert(snapshot));
    }

    /**
     * Copies only non-null properties from source to target.
     *
     * <p>Used for partial updates where null values should not overwrite
     * existing state.
     *
     * @param source source object
     * @param target target object to update
     */
    private void copyNonNullProperties(Object source, Object target) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        String[] nullPropertyNames = Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);

        BeanUtils.copyProperties(source, target, nullPropertyNames);
    }
}