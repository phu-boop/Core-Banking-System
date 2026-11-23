package com.banking.platform.audit.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.banking.platform.audit.domain.AuditRecord;
import com.banking.platform.audit.domain.AuditRecordRequest;
import com.banking.platform.audit.persistence.AuditRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AuditServiceTests {
    private AuditRepository repository;
    private AuditService service;

    @BeforeEach
    void setUp() {
        repository = mock(AuditRepository.class);
        service = new AuditService(repository);
    }

    @Test
    void duplicateEventIsReplaySafe() {
        AuditRecordRequest request = request("event-1");
        AuditRecord existing = record(request);
        when(repository.findByEventId("event-1")).thenReturn(Optional.of(existing));

        AuditRecord result = service.record(request);

        assertEquals(existing, result);
        verify(repository, never()).insert(request);
    }

    @Test
    void requiredAuditFieldsAreValidated() {
        AuditRecordRequest request = new AuditRecordRequest(
                "event-1", "", "INTERNET_BANKING", "TRANSFER_VIEW",
                "Transfer", "tx-1", null, null, "SUCCESS", "corr", Instant.now());

        assertThrows(IllegalArgumentException.class, () -> service.record(request));
    }

    private AuditRecordRequest request(String eventId) {
        return new AuditRecordRequest(
                eventId, "user-1", "INTERNET_BANKING", "TRANSFER_VIEW",
                "Transfer", "tx-1", "org-1", null, "SUCCESS",
                "corr-1", Instant.now());
    }

    private AuditRecord record(AuditRecordRequest request) {
        return new AuditRecord(UUID.randomUUID(), request.eventId(), request.actorId(),
                request.channel(), request.action(), request.resourceType(), request.resourceId(),
                request.organizationId(), request.branchId(), request.result(),
                request.correlationId(), request.occurredAt(), Instant.now());
    }
}
