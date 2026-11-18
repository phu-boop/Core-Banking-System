package com.banking.platform.servicetemplate;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.servicetemplate.web.CorrelationIdFilter;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTemplateApplicationTests {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:17.12");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("service-template.database.url", POSTGRES::getJdbcUrl);
        registry.add("service-template.database.username", POSTGRES::getUsername);
        registry.add("service-template.database.password", POSTGRES::getPassword);
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void exposesHealthAndProbeEndpoints() {
        assertThat(restTemplate.getForEntity("/actuator/health", String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(restTemplate.getForEntity("/actuator/health/liveness", String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(restTemplate.getForEntity("/actuator/health/readiness", String.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
    }

    @Test
    void preservesValidCorrelationId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdFilter.HEADER_NAME, "request-123");

        ResponseEntity<String> response =
                restTemplate.exchange("/actuator/health", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst(CorrelationIdFilter.HEADER_NAME)).isEqualTo("request-123");
    }

    @Test
    void generatesCorrelationIdWhenMissing() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);
        String correlationId = response.getHeaders().getFirst(CorrelationIdFilter.HEADER_NAME);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(UUID.fromString(correlationId)).isNotNull();
    }

    @Test
    void rejectsMalformedCorrelationId() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CorrelationIdFilter.HEADER_NAME, "invalid id");

        ResponseEntity<String> response =
                restTemplate.exchange("/actuator/health", HttpMethod.GET, new HttpEntity<>(headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
