package com.banking.platform.notification.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.notification.NotificationServiceApplication;
import com.banking.platform.notification.domain.NotificationRecord;
import com.banking.platform.notification.domain.NotificationRequest;
import com.banking.platform.notification.domain.NotificationStatus;
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
@SpringBootTest(classes = NotificationServiceApplication.class)
class NotificationRepositoryIntegrationTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.12")
            .withDatabaseName("notification_test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    NotificationRepository repository;

    @Autowired
    JdbcTemplate jdbc;

    @BeforeEach
    void clear() {
        jdbc.update("DELETE FROM notification_schema.notification_attempts");
        jdbc.update("DELETE FROM notification_schema.notifications");
    }

    @Test
    void requestAndAttemptArePersistedWithoutFinancialState() {
        String key = "notify-" + UUID.randomUUID();
        NotificationRecord record = repository.insert(new NotificationRequest(
                key, "recipient-1", "TRANSFER_COMPLETED", "EMAIL", "corr-1"));

        repository.insertAttempt(record.notificationId(), 1,
                NotificationStatus.DELIVERED, "mock-1", null);
        NotificationRecord delivered = repository.updateStatus(
                record.notificationId(), NotificationStatus.DELIVERED);

        assertThat(delivered.status()).isEqualTo(NotificationStatus.DELIVERED);
        assertThat(repository.findAttempts(record.notificationId())).hasSize(1);
        assertThat(repository.findByKey(key).orElseThrow().notificationId())
                .isEqualTo(record.notificationId());
    }
}
