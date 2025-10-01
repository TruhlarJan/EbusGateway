package com.joiner.ebus.service.converter.source;

public interface Hex {

    Byte  value();
    
    class S3 implements Hex{
        
        private static final int INDEX = 2;
        private byte[] value;

        private S3(byte[] value) {
            this.value = value;
        }

        public static S3 of(byte[] slaveData) {
            return new S3(slaveData);
        }

        @Override
        public Byte value() {
            return value[INDEX];
        }
    }

    class S4 implements Hex{
        
        private static final int INDEX = 3;
        private byte[] value;

        private S4(byte[] value) {
            this.value = value;
        }

        public static S4 of(byte[] slaveData) {
            return new S4(slaveData);
        }

        @Override
        public Byte value() {
            return value[INDEX];
        }
    }

    class S7 implements Hex{
        
        private static final int INDEX = 6;
        private byte[] value;

        private S7(byte[] value) {
            this.value = value;
        }

        public static S7 of(byte[] slaveData) {
            return new S7(slaveData);
        }

        @Override
        public Byte value() {
            return value[INDEX];
        }
    }

    class S8 implements Hex{
        
        private static final int INDEX = 7;
        private byte[] value;

        private S8(byte[] value) {
            this.value = value;
        }

        public static S8 of(byte[] slaveData) {
            return new S8(slaveData);
        }

        @Override
        public Byte value() {
            return value[INDEX];
        }
    }

    
}
