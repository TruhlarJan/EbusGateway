package com.joiner.ebus.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.joiner.ebus")
@EnableScheduling
public class EbusGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbusGatewayApplication.class, args);
    }

}
