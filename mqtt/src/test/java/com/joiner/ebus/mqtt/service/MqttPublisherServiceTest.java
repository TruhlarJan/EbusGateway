package com.joiner.ebus.mqtt.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

class MqttPublisherServiceTest {

    private MessageChannel mqttOutboundChannel;

    private ObjectMapper objectMapper;

    private MqttPublisherService service;

    @BeforeEach
    void setUp() {
        mqttOutboundChannel = mock(MessageChannel.class);
        objectMapper = mock(ObjectMapper.class);
        service = new MqttPublisherService(mqttOutboundChannel, objectMapper);
    }

    @Test
    void publish_serializesPayloadAndSendsMessageWithTopicHeader() throws Exception {
        TestPayload payload = new TestPayload("payload-value");
        when(objectMapper.writeValueAsString(payload)).thenReturn("{\"value\":\"payload-value\"}");
        when(mqttOutboundChannel.send(any(Message.class))).thenReturn(true);

        service.publish("protherm/topic", payload);

        ArgumentCaptor<Message<?>> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mqttOutboundChannel).send(messageCaptor.capture());
        Message<?> sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getPayload()).isEqualTo("{\"value\":\"payload-value\"}");
        assertThat(sentMessage.getHeaders().get(MqttHeaders.TOPIC)).isEqualTo("protherm/topic");
    }

    @Test
    void publish_whenSerializationFails_wrapsExceptionInRuntimeException() throws Exception {
        TestPayload payload = new TestPayload("payload-value");
        JsonProcessingException failure = new JsonProcessingException("boom") {
            private static final long serialVersionUID = 1L;
        };
        when(objectMapper.writeValueAsString(payload)).thenThrow(failure);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.publish("protherm/topic", payload));

        assertThat(exception).hasCause(failure);
        verifyNoInteractions(mqttOutboundChannel);
    }

    private record TestPayload(String value) {
    }
}