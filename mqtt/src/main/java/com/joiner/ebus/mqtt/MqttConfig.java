package com.joiner.ebus.mqtt;

import java.io.IOException;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import io.moquette.broker.Server;
import io.moquette.broker.config.IConfig;
import io.moquette.broker.config.MemoryConfig;

@Configuration
public class MqttConfig {

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.default-topic}")
    private String defaultTopic;

    @Bean(destroyMethod = "stopServer")
    @Profile("DEV")
    Server mqttBroker() throws IOException {
        Server server = new Server();
        MemoryConfig config = new MemoryConfig(new Properties());
        config.setProperty(IConfig.ALLOW_ANONYMOUS_PROPERTY_NAME, Boolean.TRUE.toString());
        server.startServer(config);
        return server;
    }

    // =========================
    // MQTT CLIENT FACTORY
    // =========================
    @Bean
    MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        var options = new org.eclipse.paho.client.mqttv3.MqttConnectOptions();
        options.setServerURIs(new String[]{brokerUrl});
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    // =========================
    // OUTBOUND (publish)
    // =========================
    @Bean
    MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    MessageHandler mqttOutbound(MqttPahoClientFactory mqttClientFactory) {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(clientId + "-pub", mqttClientFactory);
        handler.setAsync(true);
        handler.setDefaultTopic(defaultTopic);
        handler.setConverter(new DefaultPahoMessageConverter()); // JSON string bude ok
        return handler;
    }

    // =========================
    // INBOUND (subscribe)
    // =========================
    @Bean
    MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    MessageProducer inbound(MqttPahoClientFactory mqttClientFactory) {
        String[] topics = new String[] {"protherm/roomControlUnit/+" };
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId + "-sub", mqttClientFactory, topics);
        adapter.setOutputChannel(mqttInboundChannel());
        adapter.setQos(1);
        adapter.setConverter(new DefaultPahoMessageConverter());
        return adapter;
    }

}
