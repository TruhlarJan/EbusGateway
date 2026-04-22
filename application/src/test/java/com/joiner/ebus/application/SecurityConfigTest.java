package com.joiner.ebus.application;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Tests for {@link SecurityConfig} that validate:
 * - which endpoints require authentication
 * - which endpoints are public
 * - CSRF is enabled by default but ignored for the configured matchers
 */
@SpringBootTest(
    classes = SecurityConfigTest.TestApplication.class,
    properties = {
        "server.auth.username=test-user",
        "server.auth.password=test-password"
    }
)
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @Import({ SecurityConfig.class, UserConfig.class, TestEndpoints.class })
    static class TestApplication {
        // test-only boot app
    }

    @RestController
    public static class TestEndpoints {

        @GetMapping("/public")
        String publicGet() {
            return "public";
        }

        @PostMapping("/public")
        String publicPost() {
            return "public-post";
        }

        @GetMapping("/protherm/hello")
        String prothermGet() {
            return "protherm";
        }

        @PostMapping("/protherm/hello")
        String prothermPost() {
            return "protherm-post";
        }

        @PostMapping("/v3/api-docs/test")
        String apiDocsPost() {
            return "api-docs";
        }

        @GetMapping("/swagger-ui/index.html")
        String swaggerUi() {
            return "swagger";
        }
    }

    @Test
    void publicGet_isAccessibleWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/public"))
            .andExpect(status().isOk())
            .andExpect(content().string("public"));
    }

    @Test
    void protectedProthermGet_redirectsToLoginWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/protherm/hello"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", containsString("/login")));
    }

    @Test
    void protectedSwaggerUi_redirectsToLoginWhenUnauthenticated() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
            .andExpect(status().is3xxRedirection())
            .andExpect(header().string("Location", containsString("/login")));
    }

    @Test
    @WithMockUser
    void protectedProthermGet_isAccessibleWhenAuthenticated() throws Exception {
        mockMvc.perform(get("/protherm/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string("protherm"));
    }

    @Test
    @WithMockUser
    void csrf_isIgnoredForProthermEndpoints() throws Exception {
        // CSRF is enabled by default, but explicitly ignored for /protherm/**
        mockMvc.perform(post("/protherm/hello"))
            .andExpect(status().isOk())
            .andExpect(content().string("protherm-post"));
    }

    @Test
    void csrf_isEnforcedForOtherEndpointsByDefault() throws Exception {
        // /public is permitAll, but CSRF still applies for state-changing requests.
        mockMvc.perform(post("/public"))
            .andExpect(status().isForbidden());
    }

    @Test
    void csrf_canBeSatisfiedWithTokenForOtherEndpoints() throws Exception {
        mockMvc.perform(post("/public").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(content().string("public-post"));
    }

    @Test
    void csrf_isIgnoredForApiDocsEndpoints() throws Exception {
        // /v3/api-docs/** is ignored for CSRF and is also permitAll.
        mockMvc.perform(post("/v3/api-docs/test"))
            .andExpect(status().isOk())
            .andExpect(content().string("api-docs"));
    }
}
