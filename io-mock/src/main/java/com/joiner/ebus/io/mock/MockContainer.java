package com.joiner.ebus.io.mock;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
public class MockContainer {

    @Getter
    private Map<Long, MockData> map = new HashMap<>();

    @Getter
    class MockData {
        
        private int length;
        private byte[] slave;

        public MockData(int length, byte[] slave) {
            this.length = length;
            this.slave = slave;
        }
    }

    public void setData(Long key, int masterSize, byte[] slaveData) {
        map.put(key, new MockData(masterSize, slaveData));
    }

    public MockData getData(Long key) {
        return map.get(key);
    }
    
}
