package com.joiner.ebus.communication.protherm;

public class RoomController {

    private static final int ACKNOWLEDGE_INDEX = 2;
    
    private B5h10hOperationalData operationalData;

    /**
     * Method initializes adapted operation data.
     * @param leadTemperature
     * @param serviceTemperature
     * @param serviceBurnerBlocked
     * @param leadBurnerBlocked
     * @return initialized operation data
     */
    public OperationalData getOperationalData(
            double leadTemperature, double serviceTemperature,
            boolean serviceBurnerBlocked, boolean leadBurnerBlocked) {
        operationalData = new B5h10hOperationalData(
                toHexValue(leadTemperature),
                toHexValue(serviceTemperature),
                toBooleanValue(serviceBurnerBlocked, leadBurnerBlocked));
        return operationalData;
    }

    public Boolean getAcknowledge() {
        if (operationalData == null) {
            throw new RuntimeException("Operational data has not been set.");
        }
        Boolean acknowledgeBoolean = null;
        byte[] slaveData = operationalData.getSlaveData();
        if (slaveData != null && slaveData.length == operationalData.getSlaveSize()) {
            byte b = slaveData[ACKNOWLEDGE_INDEX];
            acknowledgeBoolean = (b == 0x01);
        }
        return acknowledgeBoolean;
    }

    public static int toHexValue(double decValue) {
        return (int) decValue * 2;
    }

    private int toBooleanValue(boolean serviceBurnerBlocked, boolean leadBurnerBlocked) {
        if (!serviceBurnerBlocked && !leadBurnerBlocked) {
            return 0x00;
        } 
        if (!serviceBurnerBlocked) {
            return 0x01;
        } 
        if (!leadBurnerBlocked) {
            return 0x02;
        }
        return 0x05;
    }

}
