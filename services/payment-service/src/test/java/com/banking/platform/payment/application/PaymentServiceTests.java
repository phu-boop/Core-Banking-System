package com.banking.platform.payment.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.payment.domain.PaymentOutcomeRequest;
import com.banking.platform.payment.domain.PaymentRecord;
import com.banking.platform.payment.domain.PaymentStatus;
import com.banking.platform.payment.domain.SubmitPaymentRequest;
import com.banking.platform.payment.persistence.PaymentRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentServiceTests {
    private PaymentRepository repository;
    private PaymentService service;

    @BeforeEach
    void setUp() {
        repository = mock(PaymentRepository.class);
        service = new PaymentService(repository);
    }

    @Test
    void timeoutOutcomeRemainsUnresolvedAndCreatesNoSettlement() {
        UUID paymentId = UUID.randomUUID();
        PaymentRecord payment = payment(paymentId, PaymentStatus.PENDING_SUBMISSION);
        when(repository.find(paymentId)).thenReturn(Optional.of(payment));

        service.recordOutcome(paymentId,
                new PaymentOutcomeRequest("UNRESOLVED", "PROVIDER_QUERY", null, "corr"));

        verify(repository).recordOutcome(eq(paymentId), any(), anyString(),
                anyString(), isNull(), anyString());
        verify(repository).updateStatus(paymentId, PaymentStatus.UNRESOLVED, null);
    }

    @Test
    void terminalOutcomeCannotBeChanged() {
        UUID paymentId = UUID.randomUUID();
        PaymentRecord settled = payment(paymentId, PaymentStatus.SETTLED);
        when(repository.find(paymentId)).thenReturn(Optional.of(settled));
        when(repository.outcomeExists(paymentId, "FAILED", "PROVIDER_CALLBACK", "ref"))
                .thenReturn(false);

        assertThrows(PaymentExceptions.ConflictException.class,
                () -> service.recordOutcome(paymentId,
                        new PaymentOutcomeRequest("FAILED", "PROVIDER_CALLBACK", "ref", "corr")));
        verify(repository, never()).updateStatus(any(), any(), any());
    }

    @Test
    void onlyVndPaymentSubmissionIsAccepted() {
        SubmitPaymentRequest request = new SubmitPaymentRequest(
                UUID.randomUUID(), "dest-1", new BigDecimal("100.00"), "USD", "corr");

        assertThrows(IllegalArgumentException.class,
                () -> service.submit(request, "idem", "actor", "corr"));
    }

    private PaymentRecord payment(UUID paymentId, PaymentStatus status) {
        Instant now = Instant.now();
        return new PaymentRecord(paymentId, UUID.randomUUID(),
                new BigDecimal("100.00"), "VND", "dest-1",
                status, null, "corr", now, now);
    }
}
