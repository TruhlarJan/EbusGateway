package com.joiner.ebus.communication.link;

import org.springframework.context.ApplicationEvent;

import com.joiner.ebus.communication.protherm.SlaveData;

import lombok.Getter;

public class SlaveDataReadyEvent extends ApplicationEvent {

    private static final long serialVersionUID = -493943632679525060L;
    
    @Getter
    private transient SlaveData slaveData;

    public SlaveDataReadyEvent(Object source, SlaveData slaveData) {
        super(source);
        this.slaveData = slaveData;
    }

}
