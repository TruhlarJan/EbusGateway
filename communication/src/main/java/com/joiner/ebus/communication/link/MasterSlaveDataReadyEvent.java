package com.joiner.ebus.communication.link;

import org.springframework.context.ApplicationEvent;

import com.joiner.ebus.communication.protherm.MasterSlaveData;

import lombok.Getter;

public class MasterSlaveDataReadyEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1187266734584588936L;

    @Getter
    private MasterSlaveData masterSlaveData;

    public MasterSlaveDataReadyEvent(Object source, MasterSlaveData masterSlaveData) {
        super(source);
        this.masterSlaveData = masterSlaveData;
    }

}
