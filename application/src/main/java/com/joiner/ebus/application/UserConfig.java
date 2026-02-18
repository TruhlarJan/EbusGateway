package com.joiner.ebus.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    @Value("${server.auth.username}")
    private String username;

    @Value("${server.auth.password}")
    private String password;

    /**
     * User credentials are in memory.
     * @return user detail
     */
    @Bean
    UserDetailsService userDetailsService() {
        var manager = new InMemoryUserDetailsManager();
        manager.createUser(User.withUsername(username)
            .password(passwordEncoder().encode(password))
            .roles("USER").build());
        return manager;
    }

    /**
     * BCryptPasswordEncoder.
     * @return BCryptPasswordEncoder
     */
    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

