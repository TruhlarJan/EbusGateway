package com.joiner.ebus.communication.link;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.protherm.Address03h15hB5h13hData;
import com.joiner.ebus.communication.protherm.Address03h64hB5h12hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h10hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h00hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h01hData;
import com.joiner.ebus.communication.protherm.Address10h08hB5h11h01h02hData;
import com.joiner.ebus.communication.protherm.AddressUnknownData;

import lombok.Getter;

@Component
public class DataEventFactory {
    
    public static int ADDRESS_SIZE = 6;

    @Autowired
    private ByteUtils utils;
    
    public ApplicationEvent getDataReadyEvent(Object source, byte[] data) {
        byte[] address = Arrays.copyOfRange(data, 0, ADDRESS_SIZE);

        long key = utils.getKey(address);
        if (key == Address10h08hB5h10hData.KEY) {
            return new Address10h08hB5h10hDataReadyEvent(address, new Address10h08hB5h10hData(data));
        } else if (key == Address10h08hB5h11h01h00hData.KEY) {
            return new Address10h08hB5h11h01h00hDataReadyEvent(address, new Address10h08hB5h11h01h00hData(data));
        } else if (key == Address10h08hB5h11h01h01hData.KEY) {
            return new Address10h08hB5h11h01h01hDataReadyEvent(address, new Address10h08hB5h11h01h01hData(data));
        } else if (key == Address10h08hB5h11h01h02hData.KEY) {
            return new Address10h08hB5h11h01h02hDataReadyEvent(address, new Address10h08hB5h11h01h02hData(data));
        } else if (key == Address03h64hB5h12hData.KEY) {
            return new Address03h64hB5h12hDataReadyEvent(address, new Address03h64hB5h12hData(data));
         } else if (key == Address03h15hB5h13hData.KEY) {
             return new Address03h15hB5h13hDataReadyEvent(address, new Address03h15hB5h13hData(data));
        } else {
            return new AddressUnknownDataReadyEvent(address, new AddressUnknownData(data));
        }
    }

    public class Address10h08hB5h10hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = 3206849664874081556L;

        @Getter
        private Address10h08hB5h10hData data;

        public Address10h08hB5h10hDataReadyEvent(Object source, Address10h08hB5h10hData data) {
            super(source);
            this.data = data;
        }
    }

    public class Address10h08hB5h11h01h00hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -5630332542112717458L;

        @Getter
        private Address10h08hB5h11h01h00hData data;

        public Address10h08hB5h11h01h00hDataReadyEvent(Object source, Address10h08hB5h11h01h00hData data) {
            super(source);
            this.data = data;
        }
    }

    public class Address10h08hB5h11h01h01hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -6576733601750592663L;

        @Getter
        private Address10h08hB5h11h01h01hData data;

        public Address10h08hB5h11h01h01hDataReadyEvent(Object source, Address10h08hB5h11h01h01hData data) {
            super(source);
            this.data = data;
        }
    }

    public class Address10h08hB5h11h01h02hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -8162007652446644868L;

        @Getter
        private Address10h08hB5h11h01h02hData data;

        public Address10h08hB5h11h01h02hDataReadyEvent(Object source, Address10h08hB5h11h01h02hData data) {
            super(source);
            this.data = data;
        }
    }

    public class Address03h64hB5h12hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = 451440166722021534L;

        @Getter
        private Address03h64hB5h12hData data;

        public Address03h64hB5h12hDataReadyEvent(Object source, Address03h64hB5h12hData data) {
            super(source);
            this.data = data;
        }
    }

    public class Address03h15hB5h13hDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = -3851909859873689083L;

        @Getter
        private Address03h15hB5h13hData data;

        public Address03h15hB5h13hDataReadyEvent(Object source, Address03h15hB5h13hData data) {
            super(source);
            this.data = data;
        }
    }

    public class AddressUnknownDataReadyEvent extends ApplicationEvent {

        private static final long serialVersionUID = 363929230694757998L;

        @Getter
        private AddressUnknownData data;

        public AddressUnknownDataReadyEvent(Object source, AddressUnknownData data) {
            super(source);
            this.data = data;
        }
    }

}
