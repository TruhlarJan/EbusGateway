package com.joiner.ebus.communication.protherm;

public interface SlaveData {

    /**
     * Address for key
     * @return
     */
    byte[] getAddress();

    /**
     * Remaining data after address.
     * @return
     */
    byte[] getData();

    /**
     * Address key of the MasterSlave object.
     * @return
     */
    long getKey();

}
