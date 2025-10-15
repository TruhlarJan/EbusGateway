package com.joiner.ebus.service.event;

import org.springframework.context.ApplicationEvent;

import com.joiner.ebus.model.BurnerControlUnitBlock0Dto;

import lombok.Getter;

public class BurnerControlUnitBlock0MqttEvent extends ApplicationEvent {

    private static final long serialVersionUID = -8327025530389823499L;

    @Getter
    private transient BurnerControlUnitBlock0Dto payload;

    public BurnerControlUnitBlock0MqttEvent(Object source, BurnerControlUnitBlock0Dto payload) {
        super(source);
        this.payload = payload;
    }
}

