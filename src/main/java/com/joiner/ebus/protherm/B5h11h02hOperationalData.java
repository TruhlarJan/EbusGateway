package com.joiner.ebus.protherm;

/**
 * eBUS Specification
 * Application Layer – OSI 7
 * Vaillant specific extensions V0.6.0
 * 
 * 3.6 B5h 11h 02h - Operational Data of Burner Control Unit to Room Control Unit.
 */
public class B5h11h02hOperationalData implements OperationalData {

    @Override
    public byte[] getMasterCrcEndedData() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public int getSlaveSize() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void setSlaveData(byte[] response) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public byte[] getSlaveData() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
