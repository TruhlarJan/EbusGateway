package com.joiner.ebus.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // CSRF enabled by default, but ignored for Swagger UI and internal REST endpoints
            .csrf(csrf -> csrf.ignoringRequestMatchers(
                "/v3/api-docs/**",       // OpenAPI JSON
                "/swagger-ui/**",        // Swagger UI
                "/protherm/**"           // Internal REST POST/PUT
            ))

            .authorizeHttpRequests(auth -> auth
                // Protect REST API and Swagger UI
                .requestMatchers("/protherm/**", "/swagger-ui/**").authenticated()

                // Everything else is public
                .anyRequest().permitAll()
            )

            // Default Spring login page
            .formLogin(form -> form
                .defaultSuccessUrl("/swagger-ui/index.html", true)
                .permitAll()
            )

            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

}
