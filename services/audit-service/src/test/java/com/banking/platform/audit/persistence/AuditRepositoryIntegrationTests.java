package com.banking.platform.audit.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.audit.AuditServiceApplication;
import com.banking.platform.audit.domain.AuditRecord;
import com.banking.platform.audit.domain.AuditRecordRequest;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = AuditServiceApplication.class)
class AuditRepositoryIntegrationTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.12")
            .withDatabaseName("audit_test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("audit.security.issuer-uri", () -> "https://issuer.example");
        registry.add("audit.security.jwk-set-uri", () -> "https://issuer.example/jwks");
        registry.add("audit.security.audience", () -> "audit-service");
    }

    @Autowired
    AuditRepository repository;

    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void clear() {
        jdbc.update("DELETE FROM audit_schema.audit_records");
    }

    @Test
    void insertIsDeduplicatedByEventId() {
        AuditRecordRequest request = new AuditRecordRequest(
                "event-" + UUID.randomUUID(), "actor-1", "INTERNET_BANKING",
                "TRANSFER_VIEW", "Transfer", UUID.randomUUID().toString(),
                "org-1", null, "SUCCESS", "corr-1", Instant.now());

        AuditRecord inserted = repository.insert(request);
        AuditRecord replay = repository.findByEventId(request.eventId()).orElseThrow();

        assertThat(replay.auditRecordId()).isEqualTo(inserted.auditRecordId());
        assertThat(repository.search("actor-1", "Transfer", null,
                "org-1", null, 100))
                .hasSize(1);
    }
}
