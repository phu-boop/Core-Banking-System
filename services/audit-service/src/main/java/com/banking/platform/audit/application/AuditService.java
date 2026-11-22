package com.banking.platform.audit.application;

import com.banking.platform.audit.domain.AuditRecord;
import com.banking.platform.audit.domain.AuditRecordRequest;
import com.banking.platform.audit.persistence.AuditRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuditService {
    private final AuditRepository repository;

    public AuditService(AuditRepository repository) {
        this.repository = repository;
    }

    public AuditRecord record(AuditRecordRequest request) {
        validate(request);
        return repository.findByEventId(request.eventId())
                .orElseGet(() -> repository.insert(request));
    }

    public List<AuditRecord> search(String actorId, String resourceType, String resourceId,
                                    String organizationId, String branchId, int limit) {
        return repository.search(actorId, resourceType, resourceId,
                organizationId, branchId, limit);
    }

    private void validate(AuditRecordRequest request) {
        if (request.eventId() == null || request.eventId().isBlank()
                || request.actorId() == null || request.actorId().isBlank()
                || request.channel() == null || request.channel().isBlank()
                || request.action() == null || request.action().isBlank()
                || request.resourceType() == null || request.resourceType().isBlank()
                || request.resourceId() == null || request.resourceId().isBlank()
                || request.result() == null || request.result().isBlank()
                || request.correlationId() == null || request.correlationId().isBlank()
                || request.occurredAt() == null) {
            throw new IllegalArgumentException("Required audit fields must be supplied");
        }
    }
}
