package com.joiner.ebus.communication.link;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.protherm.Tg0315B513Data;
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
import com.joiner.ebus.communication.protherm.TgUnknownData;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Component
public class DataEventFactory {
    
    public static int ADDRESS_SIZE = 6;

    @Autowired
    private ByteUtils utils;
    
    public Object getDataReadyEvent(byte[] data) {
        byte[] address = Arrays.copyOfRange(data, 0, ADDRESS_SIZE);

        long key = utils.getKey(address);
        if (key == Tg1008B510Data.KEY) {
            return new Tg1008B510DataReadyEvent(new Tg1008B510Data(data));
        } else if (key == Tg1008B5110100Data.KEY) {
            return new Tg1008B5110100DataReadyEvent(new Tg1008B5110100Data(data));
        } else if (key == Tg1008B5110101Data.KEY) {
            return new Tg1008B5110101DataReadyEvent(new Tg1008B5110101Data(data));
        } else if (key == Tg1008B5110102Data.KEY) {
            return new Tg1008B5110102DataReadyEvent(new Tg1008B5110102Data(data));
        } else if (key == Tg0364B512Data.KEY) {
            return new Tg0364B512DataReadyEvent(new Tg0364B512Data(data));
         } else if (key == Tg0315B513Data.KEY) {
             return new Tg0315B513DataReadyEvent(new Tg0315B513Data(data));
        } else {
            return new TgUnknownDataReadyEvent(new TgUnknownData(data));
        }
    }

    @RequiredArgsConstructor
    public class Tg1008B510DataReadyEvent {

        @Getter
        private final Tg1008B510Data data;
    }

    @RequiredArgsConstructor
    public class Tg1008B5110100DataReadyEvent {

        @Getter
        private final Tg1008B5110100Data data;
    }

    @RequiredArgsConstructor
    public class Tg1008B5110101DataReadyEvent {

        @Getter
        private final Tg1008B5110101Data data;
    }

    @RequiredArgsConstructor
    public class Tg1008B5110102DataReadyEvent {

        @Getter
        private final Tg1008B5110102Data data;
    }

    @RequiredArgsConstructor
    public class Tg0364B512DataReadyEvent {

        @Getter
        private final Tg0364B512Data data;
    }

    @RequiredArgsConstructor
    public class Tg0315B513DataReadyEvent {

        @Getter
        private final Tg0315B513Data data;
    }

    @RequiredArgsConstructor
    public class TgUnknownDataReadyEvent {

        @Getter
        private final TgUnknownData data;
    }

}
