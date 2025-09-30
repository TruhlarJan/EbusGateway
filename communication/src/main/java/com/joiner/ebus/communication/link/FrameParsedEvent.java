package com.joiner.ebus.communication.link;

import org.springframework.context.ApplicationEvent;

import com.joiner.ebus.communication.protherm.SlaveData;

import lombok.Getter;

public class FrameParsedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -493943632679525060L;
    
    @Getter
    private long key;
    
    @Getter
    private transient SlaveData slaveData;

    public FrameParsedEvent(Object source, long key, SlaveData slaveData) {
        super(source);
        this.key = key;
        this.slaveData = slaveData;
    }

}
