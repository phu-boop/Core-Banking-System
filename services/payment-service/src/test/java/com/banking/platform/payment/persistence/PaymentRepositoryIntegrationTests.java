package com.banking.platform.payment.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.payment.domain.PaymentRecord;
import com.banking.platform.payment.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
class PaymentRepositoryIntegrationTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.12")
            .withDatabaseName("payment_test")
            .withUsername("payment")
            .withPassword("payment");

    @MockBean
    JwtDecoder jwtDecoder;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.default-schema", () -> "payment_schema");
    }

    @Autowired
    PaymentRepository repository;

    @Test
    void persistsPaymentAndUnresolvedOutcomeWithoutCrossSchemaForeignKey() {
        UUID paymentId = UUID.randomUUID();
        UUID transferId = UUID.randomUUID();
        Instant now = Instant.now();
        PaymentRecord payment = new PaymentRecord(
                paymentId, transferId, new BigDecimal("125.5000"), "VND",
                "dest-reference", PaymentStatus.PENDING_SUBMISSION, null,
                "correlation", now, now);

        repository.insert(payment);
        repository.recordOutcome(paymentId, UUID.randomUUID(), "UNRESOLVED",
                "PROVIDER_QUERY", null, "correlation");
        repository.updateStatus(paymentId, PaymentStatus.UNRESOLVED, null);

        assertThat(repository.find(paymentId))
                .isPresent()
                .get()
                .extracting(PaymentRecord::status)
                .isEqualTo(PaymentStatus.UNRESOLVED);
    }

    @Test
    void idempotencyReplayReturnsExistingPaymentAndConflictPayloadUsesSeparateHash() {
        UUID paymentId = UUID.randomUUID();
        Instant now = Instant.now();
        PaymentRecord payment = new PaymentRecord(
                paymentId, UUID.randomUUID(), new BigDecimal("100.0000"), "VND",
                "dest-reference", PaymentStatus.PENDING_SUBMISSION, null,
                "correlation", now, now);

        repository.insert(payment);
        repository.saveIdempotency("idem-payment", "actor", "payment-submit",
                payment.transferId().toString(), "payload-hash", paymentId);

        assertThat(repository.findIdempotency(
                "idem-payment", "actor", "payment-submit",
                payment.transferId().toString()))
                .isPresent()
                .get()
                .extracting(record -> record.payloadHash())
                .isEqualTo("payload-hash");
    }
}
