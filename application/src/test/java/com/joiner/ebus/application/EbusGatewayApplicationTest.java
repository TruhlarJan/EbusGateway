package com.joiner.ebus.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Lightweight tests that avoid bootstrapping the full Spring context (which would
 * pull in other modules).
 */
class EbusGatewayApplicationTest {

    @Test
    void hasExpectedSpringAnnotations() {
        assertThat(EbusGatewayApplication.class.isAnnotationPresent(SpringBootApplication.class)).isTrue();
        assertThat(EbusGatewayApplication.class.isAnnotationPresent(EnableScheduling.class)).isTrue();
        assertThat(EbusGatewayApplication.class.isAnnotationPresent(EnableAsync.class)).isTrue();

        SpringBootApplication sba = EbusGatewayApplication.class.getAnnotation(SpringBootApplication.class);
        assertThat(sba).isNotNull();
        assertThat(sba.scanBasePackages()).containsExactly("com.joiner.ebus");
    }
}
