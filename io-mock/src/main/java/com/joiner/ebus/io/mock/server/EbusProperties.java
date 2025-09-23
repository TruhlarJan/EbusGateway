package com.joiner.ebus.io.mock.server;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "ebus")
@Getter
@Setter
public class EbusProperties {

    private int port;
    private int interval;
    private List<String> packets;

}
