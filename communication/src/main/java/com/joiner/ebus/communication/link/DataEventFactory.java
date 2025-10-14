package com.joiner.ebus.communication.link;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
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

@Component
public class DataEventFactory {
    
    public static int ADDRESS_SIZE = 6;

    @Autowired
    private ByteUtils utils;
    
    public ApplicationEvent getDataReadyEvent(Object source, byte[] data) {
        byte[] address = Arrays.copyOfRange(data, 0, ADDRESS_SIZE);

        long key = utils.getKey(address);
        if (key == Tg1008B510Data.KEY) {
            return new Address10h08hB5h10hDataReadyEvent(source, new Tg1008B510Data(data));
        } else if (key == Tg1008B5110100Data.KEY) {
            return new Address10h08hB5h11h01h00hDataReadyEvent(source, new Tg1008B5110100Data(data));
        } else if (key == Tg1008B5110101Data.KEY) {
            return new Address10h08hB5h11h01h01hDataReadyEvent(source, new Tg1008B5110101Data(data));
        } else if (key == Tg1008B5110102Data.KEY) {
            return new Address10h08hB5h11h01h02hDataReadyEvent(source, new Tg1008B5110102Data(data));
        } else if (key == Tg0364B512Data.KEY) {
            return new Address03h64hB5h12hDataReadyEvent(source, new Tg0364B512Data(data));
         } else if (key == Tg0315B513Data.KEY) {
             return new Address03h15hB5h13hDataReadyEvent(source, new Tg0315B513Data(data));
        } else {
            return new AddressUnknownDataReadyEvent(source, new TgUnknownData(data));
        }
    }

    public class Address10h08hB5h10hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = 3206849664874081556L;

        @Getter
        private Tg1008B510Data data;

        public Address10h08hB5h10hDataReadyEvent(Object source, Tg1008B510Data data) {
            super(source);
            this.data = data;
        }
    }

    public class Address10h08hB5h11h01h00hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -5630332542112717458L;

        @Getter
        private Tg1008B5110100Data data;

        public Address10h08hB5h11h01h00hDataReadyEvent(Object source, Tg1008B5110100Data data) {
            super(source);
            this.data = data;
        }
    }

    public class Address10h08hB5h11h01h01hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -6576733601750592663L;

        @Getter
        private Tg1008B5110101Data data;

        public Address10h08hB5h11h01h01hDataReadyEvent(Object source, Tg1008B5110101Data data) {
            super(source);
            this.data = data;
        }
    }

    public class Address10h08hB5h11h01h02hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -8162007652446644868L;

        @Getter
        private Tg1008B5110102Data data;

        public Address10h08hB5h11h01h02hDataReadyEvent(Object source, Tg1008B5110102Data data) {
            super(source);
            this.data = data;
        }
    }

    public class Address03h64hB5h12hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = 451440166722021534L;

        @Getter
        private Tg0364B512Data data;

        public Address03h64hB5h12hDataReadyEvent(Object source, Tg0364B512Data data) {
            super(source);
            this.data = data;
        }
    }

    public class Address03h15hB5h13hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -3851909859873689083L;

        @Getter
        private Tg0315B513Data data;

        public Address03h15hB5h13hDataReadyEvent(Object source, Tg0315B513Data data) {
            super(source);
            this.data = data;
        }
    }

    public class AddressUnknownDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = 363929230694757998L;

        @Getter
        private TgUnknownData data;

        public AddressUnknownDataReadyEvent(Object source, TgUnknownData data) {
            super(source);
            this.data = data;
        }
    }

}
