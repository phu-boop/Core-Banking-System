package com.banking.platform.servicetemplate;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.servicetemplate.web.CorrelationIdFilter;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceTemplateApplicationTests {

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
