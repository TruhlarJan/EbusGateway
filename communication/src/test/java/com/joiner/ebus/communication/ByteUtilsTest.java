package com.joiner.ebus.communication;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ByteUtilsTest {

    @Autowired
    private ByteUtils byteUtils;
    
    @Test
    void testBytesToLong() {
        assertEquals(4328719365L, byteUtils.getKey(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04, 0x05}));
    }

}
