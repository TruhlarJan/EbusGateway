package com.joiner.ebus.mqtt;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.util.ReflectionTestUtils;

class MqttConfigTest {

    private MqttConfig config;

    @BeforeEach
    void setUp() {
        config = new MqttConfig();
        ReflectionTestUtils.setField(config, "brokerUrl", "tcp://localhost:1883");
        ReflectionTestUtils.setField(config, "clientId", "gateway-client");
        ReflectionTestUtils.setField(config, "defaultTopic", "protherm/default");
    }

    @Test
    void hasExpectedConfigurationAnnotations() throws Exception {
        assertThat(MqttConfig.class.isAnnotationPresent(Configuration.class)).isTrue();

        Method mqttBrokerMethod = MqttConfig.class.getDeclaredMethod("mqttBroker");
        Bean bean = mqttBrokerMethod.getAnnotation(Bean.class);
        Profile profile = mqttBrokerMethod.getAnnotation(Profile.class);

        assertThat(bean).isNotNull();
        assertThat(bean.destroyMethod()).isEqualTo("stopServer");
        assertThat(profile).isNotNull();
        assertThat(profile.value()).containsExactly("DEV");
    }

    @Test
    void mqttClientFactory_usesConfiguredBrokerUrlAndCleanSession() {
        DefaultMqttPahoClientFactory factory = (DefaultMqttPahoClientFactory) config.mqttClientFactory();

        assertThat(factory.getConnectionOptions().getServerURIs()).containsExactly("tcp://localhost:1883");
        assertThat(factory.getConnectionOptions().isCleanSession()).isTrue();
    }

    @Test
    void mqttOutboundChannel_returnsDirectChannel() {
        MessageChannel channel = config.mqttOutboundChannel();

        assertThat(channel).isInstanceOf(DirectChannel.class);
    }

    @Test
    void mqttOutbound_createsAsyncHandlerWithConfiguredDefaults() {
        MessageHandler handler = config.mqttOutbound(config.mqttClientFactory());

        assertThat(handler).isInstanceOf(MqttPahoMessageHandler.class);

        MqttPahoMessageHandler mqttHandler = (MqttPahoMessageHandler) handler;
        assertThat(mqttHandler.getClientId()).isEqualTo("gateway-client-pub");
        assertThat(ReflectionTestUtils.getField(mqttHandler, "async")).isEqualTo(true);
        assertThat(ReflectionTestUtils.getField(mqttHandler, "defaultTopic")).isEqualTo("protherm/default");
        assertThat(ReflectionTestUtils.getField(mqttHandler, "converter"))
                .isInstanceOf(DefaultPahoMessageConverter.class);
    }

    @Test
    void mqttInboundChannel_returnsDirectChannel() {
        MessageChannel channel = config.mqttInboundChannel();

        assertThat(channel).isInstanceOf(DirectChannel.class);
    }

    @Test
    void inbound_createsAdapterWithConfiguredTopicQosAndConverter() {
        MessageProducer producer = config.inbound(config.mqttClientFactory());

        assertThat(producer).isInstanceOf(MqttPahoMessageDrivenChannelAdapter.class);

        MqttPahoMessageDrivenChannelAdapter adapter = (MqttPahoMessageDrivenChannelAdapter) producer;
        assertThat(ReflectionTestUtils.getField(adapter, "clientId")).isEqualTo("gateway-client-sub");
        assertThat(adapter.getTopic()).containsExactly("protherm/roomControlUnit/+");
        assertThat(adapter.getQos()).containsExactly(1);
        assertThat(adapter.getOutputChannel()).isInstanceOf(DirectChannel.class);
        assertThat(ReflectionTestUtils.getField(adapter, "converter"))
                .isInstanceOf(DefaultPahoMessageConverter.class);
    }
}