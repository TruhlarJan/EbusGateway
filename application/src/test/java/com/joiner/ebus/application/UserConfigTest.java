package com.joiner.ebus.application;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest(
    classes = UserConfig.class,
    properties = {
        "server.auth.username=test-user",
        "server.auth.password=test-password"
    }
)
class UserConfigTest {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Test
    void userDetailsServiceContainsConfiguredUser() {
        UserDetails user = userDetailsService.loadUserByUsername("test-user");

        assertThat(user.getUsername()).isEqualTo("test-user");
        assertThat(user.getAuthorities()).extracting("authority").contains("ROLE_USER");
        assertThat(passwordEncoder.matches("test-password", user.getPassword())).isTrue();
    }

    @Test
    void passwordEncoderIsBCryptAndMatches() {
        String encoded = passwordEncoder.encode("abc");
        assertThat(encoded).startsWith("$2");
        assertThat(passwordEncoder.matches("abc", encoded)).isTrue();
    }
}
