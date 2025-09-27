package com.joiner.ebus.communication;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

public class FrameReceivedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -493943632679525060L;
    
    @Getter
    private MasterData slaveOperationalData;

    public FrameReceivedEvent(Object source, MasterData slaveOperationalData) {
        super(source);
        this.slaveOperationalData = slaveOperationalData;
    }

}
