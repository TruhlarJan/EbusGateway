package com.joiner.ebus.communication.protherm;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

public class FrameReceivedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -493943632679525060L;
    
    @Getter
    private final byte[] address;
    
    @Getter
    private final byte[] data;

    public FrameReceivedEvent(Object source, byte[] address, byte[] data) {
        super(source);
        this.address = address;
        this.data = data;
    }
}
