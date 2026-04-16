package com.joiner.ebus.communication.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for mock servers used in development/testing.
 * @author joiner
 */
@Configuration
public class MockConfig {

    /**
     * Creates and starts the Protherm Ebus mock server for development purposes.
     * The server will be automatically stopped when the Spring context is destroyed.
     * 
     * @return configured and started ProthermEbusMockServer instance
     */
    @Bean(destroyMethod = "stop")
    @Profile("DEV")
    ProthermEbusMockServer prothermEbusMock() {
        ProthermEbusMockServer server = new ProthermEbusMockServer();
        server.start();
        return server;
    }
}
