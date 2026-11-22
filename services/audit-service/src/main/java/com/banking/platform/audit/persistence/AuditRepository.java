package com.banking.platform.audit.persistence;

import com.banking.platform.audit.domain.AuditRecord;
import com.banking.platform.audit.domain.AuditRecordRequest;
import java.util.List;
import java.util.Optional;

public interface AuditRepository {
    Optional<AuditRecord> findByEventId(String eventId);
    AuditRecord insert(AuditRecordRequest request);
    List<AuditRecord> search(String actorId, String resourceType, String resourceId,
                             String organizationId, String branchId, int limit);
}
