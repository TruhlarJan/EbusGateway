package com.joiner.ebus.communication.link;

import org.springframework.context.ApplicationEvent;

import com.joiner.ebus.communication.protherm.MasterData;

import lombok.Getter;

public class FrameParsedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -493943632679525060L;
    
    @Getter
    private long key;
    
    @Getter
    private transient MasterData masterData;

    public FrameParsedEvent(Object source, long key, MasterData masterData) {
        super(source);
        this.key = key;
        this.masterData = masterData;
    }

}
