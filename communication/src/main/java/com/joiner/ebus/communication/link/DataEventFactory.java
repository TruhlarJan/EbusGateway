package com.joiner.ebus.communication.link;

import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
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
   
    public Object getDataReadyEvent(MasterSlaveData masterSlaveData) {
        if (masterSlaveData instanceof Tg1008B510Data tg1008b510Data) {
            return new Tg1008B510DataReadyEvent(tg1008b510Data);
        } else if (masterSlaveData instanceof Tg1008B5110100Data tg1008b5110100Data) {
            return new Tg1008B5110100DataReadyEvent(tg1008b5110100Data);
        } else if (masterSlaveData instanceof Tg1008B5110101Data tg1008b5110101Data) {
            return new Tg1008B5110101DataReadyEvent(tg1008b5110101Data);
        } else if (masterSlaveData instanceof Tg1008B5110102Data tg1008b5110102Data) {
            return new Tg1008B5110102DataReadyEvent(tg1008b5110102Data);
		} else {
			return new MasterSlaveDataReadyEvent(masterSlaveData);
		}
    }

    public Object getDataReadyEvent(MasterData masterData) {
        if (masterData instanceof Tg0364B512Data tg0364b512Data) {
            return new Tg0364B512DataReadyEvent(tg0364b512Data);
         } else if (masterData instanceof Tg0315B513Data tg0315b513Data) {
             return new Tg0315B513DataReadyEvent(tg0315b513Data);
        } else if (masterData instanceof TgUnknownData tgUnknownData) {
            return new TgUnknownDataReadyEvent(tgUnknownData);
		} else {
			return new MasterDataReadyEvent(masterData);
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

    @RequiredArgsConstructor
    public class MasterSlaveDataReadyEvent {

        @Getter
        private final MasterSlaveData data;
    }

    @RequiredArgsConstructor
    public class MasterDataReadyEvent {

        @Getter
        private final MasterData data;
    }
    
}
