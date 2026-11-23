package com.banking.platform.reporting.web;

import com.banking.platform.reporting.application.ReportingAuthorizationService;
import com.banking.platform.reporting.application.ReportingService;
import com.banking.platform.reporting.domain.ExportJob;
import com.banking.platform.reporting.domain.ExportRequest;
import com.banking.platform.reporting.domain.ReportRequest;
import com.banking.platform.reporting.domain.ReportResponse;
import java.time.Instant;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportingController {
    private final ReportingService service;
    private final ReportingAuthorizationService authorization;

    public ReportingController(ReportingService service,
                               ReportingAuthorizationService authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @GetMapping("/v1/reports/{reportType}")
    public ReportResponse query(
            @PathVariable String reportType,
            @RequestParam(required = false) String organizationId,
            @RequestParam(required = false) String accountId,
            @RequestParam(required = false) String branchId,
            @RequestParam(required = false) String channel,
            @RequestParam Instant from,
            @RequestParam Instant to,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "100") int pageSize,
            @AuthenticationPrincipal Jwt jwt) {
        authorization.requireRead(jwt, reportType);
        authorization.requireScope(jwt, organizationId, accountId, branchId);
        return service.query(reportType, new ReportRequest(
                organizationId, accountId, branchId, channel,
                from, to, page, pageSize));
    }

    @PostMapping("/v1/reports/{reportType}/exports")
    public ExportJob export(@PathVariable String reportType,
                            @RequestBody ExportRequest request,
                            @AuthenticationPrincipal Jwt jwt) {
        authorization.requireRead(jwt, reportType);
        authorization.requireScope(jwt, request.organizationId(),
                request.accountId(), request.branchId());
        return service.export(reportType, request);
    }
}
