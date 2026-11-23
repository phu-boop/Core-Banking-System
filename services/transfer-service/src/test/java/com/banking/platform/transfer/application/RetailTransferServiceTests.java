package com.banking.platform.transfer.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.transfer.domain.RetailTransferRecord;
import com.banking.platform.transfer.domain.RetailTransferRequest;
import com.banking.platform.transfer.domain.RetailTransferType;
import com.banking.platform.transfer.domain.TransferStatus;
import com.banking.platform.transfer.integration.LedgerExecutionClient;
import com.banking.platform.transfer.persistence.RetailTransferRepository;
import com.banking.platform.transfer.persistence.RetailTransferRepository.RetailIdempotencyRecord;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.ResourceAccessException;

@ExtendWith(MockitoExtension.class)
class RetailTransferServiceTests {
    @Mock
    RetailTransferRepository repository;

    @Mock
    LedgerExecutionClient ledger;

    @Test
    void settledAfterLedgerPost() {
        UUID transferId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        UUID journalId = UUID.randomUUID();
        RetailTransferRequest request = validRequest();
        when(repository.findReplay(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(ledger.postInternalJournal(any(), any(), any(), any(), anyString(), anyString()))
                .thenReturn(journalId);
        when(repository.find(any())).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return Optional.of(new RetailTransferRecord(
                    id, transactionId, RetailTransferType.INTERNAL,
                    request.sourceAccountId(), request.destinationAccountId(),
                    request.processingBranchId(), request.amount(), "VND",
                    TransferStatus.SETTLED, journalId, "corr",
                    java.time.Instant.now(), java.time.Instant.now()));
        });

        RetailTransferService service = new RetailTransferService(repository, ledger);
        RetailTransferRecord result = service.submit(request, "idem-1", "maker", "corr");

        assertThat(result.status()).isEqualTo(TransferStatus.SETTLED);
        assertThat(result.ledgerJournalId()).isEqualTo(journalId);
        verify(repository).saveIdempotency(anyString(), anyString(), anyString(),
                anyString(), anyString(), any());
    }

    @Test
    void conflictingIdempotencyPayloadIsRejected() {
        RetailTransferRequest request = validRequest();
        when(repository.findReplay(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.of(new RetailIdempotencyRecord(
                        UUID.randomUUID(), "different-hash")));

        RetailTransferService service = new RetailTransferService(repository, ledger);
        assertThatThrownBy(() -> service.submit(request, "idem-1", "maker", "corr"))
                .isInstanceOf(TransferExceptions.ConflictException.class);
        verify(ledger, never()).postInternalJournal(any(), any(), any(), any(), anyString(), anyString());
    }

    @Test
    void ledgerTimeoutRemainsProcessing() {
        UUID transactionId = UUID.randomUUID();
        RetailTransferRequest request = validRequest();
        when(repository.findReplay(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());
        when(ledger.postInternalJournal(any(), any(), any(), any(), anyString(), anyString()))
                .thenThrow(new ResourceAccessException("timeout"));
        when(repository.find(any())).thenAnswer(invocation -> {
            UUID id = invocation.getArgument(0);
            return Optional.of(new RetailTransferRecord(
                    id, transactionId, RetailTransferType.INTERNAL,
                    request.sourceAccountId(), request.destinationAccountId(),
                    request.processingBranchId(), request.amount(), "VND",
                    TransferStatus.PROCESSING, null, "corr",
                    java.time.Instant.now(), java.time.Instant.now()));
        });

        RetailTransferService service = new RetailTransferService(repository, ledger);
        assertThat(service.submit(request, "idem-1", "maker", "corr").status())
                .isEqualTo(TransferStatus.PROCESSING);
    }

    private RetailTransferRequest validRequest() {
        return new RetailTransferRequest(
                RetailTransferType.INTERNAL,
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                "VND");
    }
}
