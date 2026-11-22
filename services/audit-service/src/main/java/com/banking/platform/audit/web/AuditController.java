package com.banking.platform.audit.web;

import com.banking.platform.audit.application.AuditAuthorizationService;
import com.banking.platform.audit.application.AuditService;
import com.banking.platform.audit.domain.AuditRecord;
import com.banking.platform.audit.domain.AuditRecordRequest;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuditController {
    private final AuditService service;
    private final AuditAuthorizationService authorization;

    public AuditController(AuditService service,
                           AuditAuthorizationService authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @PostMapping("/internal/audit/records")
    public AuditRecord record(@RequestBody AuditRecordRequest request,
                              @AuthenticationPrincipal Jwt jwt) {
        authorization.requireWrite(jwt);
        return service.record(request);
    }

    @GetMapping("/v1/audit/records")
    public List<AuditRecord> search(
            @RequestParam(required = false) String actorId,
            @RequestParam(required = false) String resourceType,
            @RequestParam(required = false) String resourceId,
            @RequestParam(required = false) String organizationId,
            @RequestParam(required = false) String branchId,
            @RequestParam(defaultValue = "100") int limit,
            @AuthenticationPrincipal Jwt jwt) {
        authorization.requireRead(jwt);
        return service.search(actorId, resourceType, resourceId,
                organizationId, branchId, limit);
    }
}
