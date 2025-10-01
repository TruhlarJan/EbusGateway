package com.joiner.ebus.service.converter.source;

import lombok.Getter;

@Getter
public class Booleans {

    private boolean b0;
    private boolean b2;

    public Booleans(boolean b0, boolean b2) {
        this.b0 = b0;
        this.b2 = b2;
    }

    public static Booleans of(boolean b0, boolean b2) {
        return new Booleans(b0, b2);
    }

}
