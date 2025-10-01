package com.joiner.ebus.service.converter.source;

public interface Bit {

    Integer value();

    class S9b0 implements Bit {
        
        private static final int INDEX = 8;
        private int value;

        private S9b0(byte[] value) {
            this.value = value[INDEX] & 1;
        }

        public static S9b0 of(byte[] slaveData) {
            return new S9b0(slaveData);
        }

        @Override
        public Integer value() {
            return value;
        }
    }

    class S9b2 implements Bit {
        
        private static final int INDEX = 8;
        private int value;

        private S9b2(byte[] value) {
            this.value = value[INDEX] >> 2 & 1;
        }

        public static S9b2 of(byte[] slaveData) {
            return new S9b2(slaveData);
        }

        @Override
        public Integer value() {
            return value;
        }
    }

}
