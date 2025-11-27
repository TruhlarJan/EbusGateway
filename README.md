## EbusGateway 1.4.0
Gateway between eBUS and MQTT for easy integration of Protherm boilers into a smart home.
REST, MQTT supported. 

#### Supported Addresses:

##### 10 08 B5 10 - RoomControlUnit (/protherm/roomControlUnit)
* leadWaterTargetTemperature
* serviceWaterTargetTemperature
* leadWaterHeatingBlocked
* serviceWaterHeatingBlocked

##### 10 08 B5 11 01 00 - BurnerControlUnit Block0 (/protherm/burnerControlUnits/block0)
* primaryTemperature
* waterPressure
* flameBurningPower

##### 10 08 B5 11 01 01 - BurnerControlUnit Block1 (/protherm/burnerControlUnits/block1)
* leadWaterTemperature
* returnWaterTemperature
* serviceWaterTemperature
* heatingOn
* serviceWaterOn

##### 10 08 B5 11 01 02 - BurnerControlUnit Block2 (/protherm/burnerControlUnits/block2)
* heatingEnabled
* serviceWaterEnabled

##### 03 64 B5 12 - HeaterController (/protherm/heaterController)
* waterCirculatingPump

##### 03 15 B5 13 - FiringAutomat (/protherm/firingAutomat)
* internalPump

##### Unknown (/protherm/unknowns)
* data


![Component diagram](component_diagram.jpg)
