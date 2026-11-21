package com.banking.platform.transfer.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.transfer.domain.TransferRecord;
import com.banking.platform.transfer.domain.TransferStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransferRepositoryIntegrationTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.12")
            .withDatabaseName("transfer_test")
            .withUsername("transfer")
            .withPassword("transfer");

    @MockBean
    JwtDecoder jwtDecoder;

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.default-schema", () -> "transfer_schema");
    }

    @Autowired
    TransferRepository repository;

    @Test
    void persistsAndReadsTransferWithoutCrossSchemaForeignKey() {
        UUID transferId = UUID.randomUUID();
        Instant now = Instant.now();
        TransferRecord transfer = new TransferRecord(
                transferId, UUID.randomUUID(), "CORPORATE_OUTBOUND", "operation-ref",
                new BigDecimal("125.5000"), "USD", UUID.randomUUID(), 1,
                "PENDING", TransferStatus.PENDING_APPROVAL, "maker", "correlation",
                now, now);

        repository.insert(transfer);

        assertThat(repository.find(transferId))
                .isPresent()
                .get()
                .extracting(TransferRecord::status)
                .isEqualTo(TransferStatus.PENDING_APPROVAL);
    }
}
