package com.banking.platform.ledger.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.banking.platform.ledger.LedgerServiceApplication;
import com.banking.platform.ledger.domain.EntrySide;
import com.banking.platform.ledger.domain.JournalEntryRequest;
import com.banking.platform.ledger.domain.JournalRequest;
import com.banking.platform.ledger.domain.JournalResponse;
import com.banking.platform.ledger.domain.ReversalRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(classes = LedgerServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LedgerEndpointIntegrationTests {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.12")
            .withDatabaseName("ledger_test");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    TestRestTemplate rest;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void postsReplayAndReversesJournal() {
        UUID transactionId = UUID.randomUUID();
        UUID debitAccount = UUID.randomUUID();
        UUID creditAccount = UUID.randomUUID();
        JournalRequest request = new JournalRequest(transactionId, "op-" + UUID.randomUUID(), "VND", List.of(
                new JournalEntryRequest(debitAccount, EntrySide.DEBIT, new BigDecimal("100.00")),
                new JournalEntryRequest(creditAccount, EntrySide.CREDIT, new BigDecimal("100.00"))));

        var posted = rest.postForEntity("/internal/ledger/journals", request, JournalResponse.class);
        assertThat(posted.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        UUID journalId = posted.getBody().journalId();

        var replay = rest.postForEntity("/internal/ledger/journals", request, JournalResponse.class);
        assertThat(replay.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(replay.getBody().journalId()).isEqualTo(journalId);

        var reversed = rest.postForEntity(
                "/internal/ledger/journals/" + journalId + "/reversals",
                new ReversalRequest(UUID.randomUUID(), "CORRECTION", "reverse-" + UUID.randomUUID()),
                JournalResponse.class);
        assertThat(reversed.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM ledger_schema.journal_entry WHERE journal_id IN (?, ?)",
                Integer.class, journalId, reversed.getBody().journalId())).isEqualTo(4);
    }

    @Test
    void concurrentReplayPostsOneJournalAndOneSetOfEntries() throws Exception {
        String operationKey = "concurrent-" + UUID.randomUUID();
        JournalRequest request = new JournalRequest(
                UUID.randomUUID(), operationKey, "VND", List.of(
                new JournalEntryRequest(UUID.randomUUID(), EntrySide.DEBIT, new BigDecimal("100.00")),
                new JournalEntryRequest(UUID.randomUUID(), EntrySide.CREDIT, new BigDecimal("100.00"))));
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try (var executor = Executors.newFixedThreadPool(2)) {
            Callable<org.springframework.http.ResponseEntity<JournalResponse>> submit = () -> {
                ready.countDown();
                if (!start.await(5, TimeUnit.SECONDS)) {
                    throw new IllegalStateException("Concurrent requests did not reach the start barrier");
                }
                return rest.postForEntity("/internal/ledger/journals", request, JournalResponse.class);
            };
            var first = executor.submit(submit);
            var second = executor.submit(submit);
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();
            start.countDown();

            var firstResponse = first.get(10, TimeUnit.SECONDS);
            var secondResponse = second.get(10, TimeUnit.SECONDS);
            assertThat(firstResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(secondResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(firstResponse.getBody().journalId())
                    .isEqualTo(secondResponse.getBody().journalId());
        }

        assertThat(jdbcTemplate.queryForObject(
                "SELECT count(*) FROM ledger_schema.journal WHERE operation_key = ?",
                Integer.class, operationKey)).isEqualTo(1);
        assertThat(jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM ledger_schema.journal_entry e
                JOIN ledger_schema.journal j ON j.journal_id = e.journal_id
                WHERE j.operation_key = ?
                """, Integer.class, operationKey)).isEqualTo(2);
        assertThat(jdbcTemplate.queryForObject("""
                SELECT count(*)
                FROM ledger_schema.ledger_audit a
                JOIN ledger_schema.journal j ON j.journal_id::text = a.resource_id
                WHERE j.operation_key = ? AND a.action = 'POST_JOURNAL'
                """, Integer.class, operationKey)).isEqualTo(1);
    }
}
