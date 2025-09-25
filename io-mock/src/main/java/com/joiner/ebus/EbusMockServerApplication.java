package com.joiner.ebus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EbusMockServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbusMockServerApplication.class, args);
    }

}
