package com.joiner.ebus.communication.link;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joiner.ebus.communication.ByteUtils;
import com.joiner.ebus.communication.protherm.MasterData;
import com.joiner.ebus.communication.protherm.MasterSlaveData;
import com.joiner.ebus.communication.protherm.Tg0315B513Data;
import com.joiner.ebus.communication.protherm.Tg0364B512Data;
import com.joiner.ebus.communication.protherm.Tg1008B510Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110100Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110101Data;
import com.joiner.ebus.communication.protherm.Tg1008B5110102Data;
import com.joiner.ebus.communication.protherm.TgUnknownData;

@Component
public class DataParser {

    @Autowired
    private ByteUtils utils;

	public MasterSlaveData getMasterSlaveData(byte[] data) {
		try {
	        long key = utils.getKey(data);
	        if (key == Tg1008B510Data.KEY) {
	            return new Tg1008B510Data(data);
	        } else if (key == Tg1008B5110100Data.KEY) {
	            return new Tg1008B5110100Data(data);
	        } else if (key == Tg1008B5110101Data.KEY) {
	            return new Tg1008B5110101Data(data);
	        } else if (key == Tg1008B5110102Data.KEY) {
	            return new Tg1008B5110102Data(data);
	        } else {
	            // UNKNOWN – unknown telegram longer or equal to 6
	            return null;
	        }
		} catch (Exception e) {
		    // INCOMPLETE – the telegram is shorter than the expected format
		    // or
		    // INVALID_CRC – the telegram has the correct length/structure, but the CRC does not match
			return null;
		}
	}

	public MasterData getMasterData(byte[] data) {
		try {
	        long key = utils.getKey(data);
	        if (key == Tg0364B512Data.KEY) {
	            return new Tg0364B512Data(data);
	         } else if (key == Tg0315B513Data.KEY) {
	             return new Tg0315B513Data(data);
	        } else {
	            // UNKNOWN – unknown telegram longer or equal to 6
	            return new TgUnknownData(data);
	        }
		} catch (Exception e) {
		    // UNKNOWN – unknown telegram shorter than 6
		    return new TgUnknownData(data);
		}
	}

}
