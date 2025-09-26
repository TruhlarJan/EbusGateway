package com.joiner.ebus.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.joiner.ebus")
public class EbusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbusGatewayApplication.class, args);
    }

}
