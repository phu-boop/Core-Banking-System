package com.banking.platform.transfer.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.transfer.domain.SubmitTransferRequest;
import com.banking.platform.transfer.domain.TransferRecord;
import com.banking.platform.transfer.domain.TransferStatus;
import com.banking.platform.transfer.persistence.TransferRepository.IdempotencyRecord;
import com.banking.platform.transfer.integration.CorporateApprovalClient;
import com.banking.platform.transfer.integration.PaymentSubmissionClient;
import com.banking.platform.transfer.domain.CorporateApprovalResponse;
import com.banking.platform.transfer.domain.PaymentSubmissionResponse;
import com.banking.platform.transfer.persistence.TransferRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApprovalEnforcedTransferServiceTests {
    private TransferRepository repository;
    private CorporateApprovalClient corporate;
    private ExecutionAdapter execution;
    private ApprovalEnforcedTransferService service;

    @BeforeEach
    void setUp() {
        repository = mock(TransferRepository.class);
        corporate = mock(CorporateApprovalClient.class);
        execution = mock(ExecutionAdapter.class);
        service = new ApprovalEnforcedTransferService(repository, corporate, execution);
    }

    @Test
    void pendingApprovalCannotRequestExecution() {
        UUID transferId = UUID.randomUUID();
        TransferRecord pending = transfer(transferId, TransferStatus.PENDING_APPROVAL);
        when(repository.find(transferId)).thenReturn(Optional.of(pending));

        assertThrows(TransferExceptions.ConflictException.class,
                () -> service.requestExecution(transferId, "maker", "corr"));
        verify(execution, never()).requestExecution(any());
    }

    @Test
    void rejectedApprovalCannotRequestExecution() {
        UUID transferId = UUID.randomUUID();
        TransferRecord rejected = transfer(transferId, TransferStatus.REJECTED);
        when(repository.find(transferId)).thenReturn(Optional.of(rejected));

        assertThrows(TransferExceptions.ConflictException.class,
                () -> service.requestExecution(transferId, "maker", "corr"));
        verify(execution, never()).requestExecution(any());
    }

    @Test
    void approvedTransferReachesExecutionAdapterBoundary() {
        UUID transferId = UUID.randomUUID();
        TransferRecord approved = transfer(transferId, TransferStatus.APPROVED);
        when(repository.find(transferId)).thenReturn(Optional.of(approved));
        ApprovalEnforcedTransferService boundaryService =
                new ApprovalEnforcedTransferService(
                        repository, corporate, new ExecutionAdapter.DeferredExecutionAdapter());

        assertThrows(UnsupportedOperationException.class,
                () -> boundaryService.requestExecution(transferId, "maker", "corr"));
    }

    @Test
    void conflictingIdempotencyPayloadIsRejected() {
        SubmitTransferRequest request = new SubmitTransferRequest(
                UUID.randomUUID(), "CORPORATE_OUTBOUND", "operation-1",
                new BigDecimal("100.00"), "USD");
        UUID existingTransferId = UUID.randomUUID();
        when(repository.findIdempotentTransfer(
                "idem-1", "maker", request.organizationId(), "corporate-transfer-submit"))
                .thenReturn(Optional.of(new IdempotencyRecord(
                        existingTransferId, "different-payload-hash")));

        assertThrows(TransferExceptions.ConflictException.class,
                () -> service.submit(request, "idem-1", "maker", "token", "corr"));
    }

    @Test
    void sameIdempotencyPayloadReplaysPriorTransfer() throws Exception {
        SubmitTransferRequest request = new SubmitTransferRequest(
                UUID.randomUUID(), "CORPORATE_OUTBOUND", "operation-1",
                new BigDecimal("100.00"), "USD");
        UUID existingTransferId = UUID.randomUUID();
        TransferRecord existing = transfer(existingTransferId, TransferStatus.PENDING_APPROVAL);
        String payloadHash = java.util.HexFormat.of().formatHex(
                java.security.MessageDigest.getInstance("SHA-256").digest(
                        (request.organizationId() + "|CORPORATE_OUTBOUND|operation-1|100.00|USD")
                                .getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        when(repository.findIdempotentTransfer(
                "idem-1", "maker", request.organizationId(), "corporate-transfer-submit"))
                .thenReturn(Optional.of(new IdempotencyRecord(existingTransferId, payloadHash)));
        when(repository.find(existingTransferId)).thenReturn(Optional.of(existing));

        TransferRecord replay = service.submit(
                request, "idem-1", "maker", "token", "corr");

        assertEquals(existingTransferId, replay.transferId());
        verify(corporate, never()).createRequest(any(), any(), any(), any(), any());
    }

    @Test
    void approvedInterbankTransferSubmitsWithoutLedgerExecution() {
        repository = mock(TransferRepository.class);
        corporate = mock(CorporateApprovalClient.class);
        execution = mock(ExecutionAdapter.class);
        PaymentSubmissionClient payment = mock(PaymentSubmissionClient.class);
        service = new ApprovalEnforcedTransferService(repository, corporate, execution, payment);

        UUID organizationId = UUID.randomUUID();
        UUID approvalId = UUID.randomUUID();
        UUID paymentId = UUID.randomUUID();
        SubmitTransferRequest request = new SubmitTransferRequest(
                organizationId, "INTERBANK", "dest-1",
                new BigDecimal("100.00"), "VND");
        CorporateApprovalResponse approval = new CorporateApprovalResponse(
                approvalId, organizationId, "INTERBANK", "dest-1", 1,
                "APPROVED", null, Instant.now(), Instant.now());
        when(corporate.createRequest(any(), any(), any(), any(), any())).thenReturn(approval);
        when(payment.submit(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PaymentSubmissionResponse(
                        paymentId, "PENDING_SUBMISSION", null, "corr"));
        when(repository.find(any())).thenAnswer(invocation ->
                Optional.of(transfer((UUID) invocation.getArgument(0),
                        TransferStatus.PENDING_INTERBANK)));

        TransferRecord result = service.submit(
                request, "idem-interbank", "maker", "token", "corr");

        verify(payment).submit(any(), any(), any(), any(), any(), any());
        verify(repository).updatePayment(any(), eq(paymentId),
                eq("PENDING_SUBMISSION"), eq(TransferStatus.PENDING_INTERBANK));
        verify(execution, never()).requestExecution(any());
    }

    private TransferRecord transfer(UUID transferId, TransferStatus status) {
        Instant now = Instant.now();
        return new TransferRecord(
                transferId, UUID.randomUUID(), "CORPORATE_OUTBOUND", "op-1",
                new BigDecimal("100.00"), "USD", UUID.randomUUID(), 1,
                status == TransferStatus.REJECTED ? "REJECTED" : "PENDING",
                status, "maker", "corr", now, now);
    }
}
