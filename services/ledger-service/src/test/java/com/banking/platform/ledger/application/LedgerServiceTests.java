package com.banking.platform.ledger.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.ledger.domain.EntrySide;
import com.banking.platform.ledger.domain.JournalEntryRequest;
import com.banking.platform.ledger.domain.JournalRequest;
import com.banking.platform.ledger.domain.JournalResponse;
import com.banking.platform.ledger.persistence.LedgerRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LedgerServiceTests {
    @Mock
    LedgerRepository repository;

    @Test
    void rejectsUnbalancedJournal() {
        UUID accountA = UUID.randomUUID();
        UUID accountB = UUID.randomUUID();
        JournalRequest request = new JournalRequest(UUID.randomUUID(), "op-1", "VND", List.of(
                new JournalEntryRequest(accountA, EntrySide.DEBIT, new BigDecimal("100.00")),
                new JournalEntryRequest(accountB, EntrySide.CREDIT, new BigDecimal("90.00"))));

        LedgerService service = new LedgerService(repository);
        assertThatThrownBy(() -> service.post(request, "actor", "corr"))
                .isInstanceOf(LedgerService.UnbalancedJournalException.class);
    }

    @Test
    void replayReturnsExistingJournal() {
        UUID journalId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        JournalRequest request = new JournalRequest(transactionId, "op-1", "VND", List.of(
                new JournalEntryRequest(UUID.randomUUID(), EntrySide.DEBIT, new BigDecimal("100.00")),
                new JournalEntryRequest(UUID.randomUUID(), EntrySide.CREDIT, new BigDecimal("100.00"))));
        JournalResponse existing = new JournalResponse(journalId, transactionId, "POSTED",
                OffsetDateTime.now(), List.of());
        when(repository.findByOperationKey("op-1")).thenReturn(Optional.of(existing));

        LedgerService service = new LedgerService(repository);
        assertThat(service.post(request, "actor", "corr")).isEqualTo(existing);
    }

    @Test
    void postsBalancedJournal() {
        UUID transactionId = UUID.randomUUID();
        JournalRequest request = new JournalRequest(transactionId, "op-1", "VND", List.of(
                new JournalEntryRequest(UUID.randomUUID(), EntrySide.DEBIT, new BigDecimal("100.00")),
                new JournalEntryRequest(UUID.randomUUID(), EntrySide.CREDIT, new BigDecimal("100.00"))));
        JournalResponse posted = new JournalResponse(UUID.randomUUID(), transactionId, "POSTED",
                OffsetDateTime.now(), List.of());
        when(repository.findByOperationKey("op-1")).thenReturn(Optional.empty());
        when(repository.post(any(), any(), any(), any(), any(), any(), any())).thenReturn(posted);

        LedgerService service = new LedgerService(repository);
        assertThat(service.post(request, "actor", "corr")).isEqualTo(posted);
        verify(repository).post(any(), any(), any(), any(), any(), any(), any());
    }
}
