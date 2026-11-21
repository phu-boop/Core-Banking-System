package com.banking.platform.transfer.web;

import com.banking.platform.transfer.application.ApprovalEnforcedTransferService;
import com.banking.platform.transfer.application.RetailTransferService;
import com.banking.platform.transfer.domain.RetailTransferRequest;
import com.banking.platform.transfer.domain.RetailTransferRecord;
import com.banking.platform.transfer.domain.SubmitTransferRequest;
import com.banking.platform.transfer.domain.TransferRecord;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/transfers")
public class TransferController {
    private final ApprovalEnforcedTransferService service;
    private final RetailTransferService retailService;

    public TransferController(ApprovalEnforcedTransferService service,
                              RetailTransferService retailService) {
        this.service = service;
        this.retailService = retailService;
    }

    @PostMapping("/internal")
    public RetailTransferRecord submitInternal(@RequestBody RetailTransferRequest request,
                                               @RequestHeader(name = "Idempotency-Key") String idempotencyKey,
                                               @AuthenticationPrincipal Jwt jwt,
                                               HttpServletRequest servletRequest) {
        return retailService.submit(request, idempotencyKey, jwt.getSubject(),
                correlationId(servletRequest));
    }

    @GetMapping("/internal/{transferId}")
    public RetailTransferRecord getInternal(@PathVariable UUID transferId) {
        return retailService.get(transferId);
    }

    @PostMapping
    public TransferRecord submit(@RequestBody SubmitTransferRequest request,
                                 @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
                                 @AuthenticationPrincipal Jwt jwt,
                                 HttpServletRequest servletRequest) {
        return service.submit(request, idempotencyKey, jwt.getSubject(),
                jwt.getTokenValue(), correlationId(servletRequest));
    }

    @GetMapping("/{transferId}")
    public TransferRecord get(@PathVariable UUID transferId) {
        return service.get(transferId);
    }

    @PostMapping("/{transferId}/approval/refresh")
    public TransferRecord refresh(@PathVariable UUID transferId,
                                  @RequestBody(required = false) SubmitTransferRequest ignored,
                                  @AuthenticationPrincipal Jwt jwt,
                                  HttpServletRequest servletRequest) {
        TransferRecord transfer = service.get(transferId);
        return service.refreshApproval(transfer.organizationId(), transferId,
                jwt.getTokenValue(), jwt.getSubject(), correlationId(servletRequest));
    }

    @PostMapping("/{transferId}/payment/refresh")
    public TransferRecord refreshPayment(@PathVariable UUID transferId,
                                         @AuthenticationPrincipal Jwt jwt,
                                         HttpServletRequest servletRequest) {
        return service.refreshPayment(transferId, jwt.getTokenValue(),
                jwt.getSubject(), correlationId(servletRequest));
    }

    @PostMapping("/{transferId}/execute")
    public TransferRecord execute(@PathVariable UUID transferId,
                                  @AuthenticationPrincipal Jwt jwt,
                                  HttpServletRequest servletRequest) {
        return service.requestExecution(transferId, jwt.getSubject(), correlationId(servletRequest));
    }

    @PostMapping("/{transferId}/cancel")
    public TransferRecord cancel(@PathVariable UUID transferId) {
        service.cancel(transferId);
        return service.get(transferId);
    }

    private String correlationId(HttpServletRequest request) {
        Object value = request.getAttribute(CorrelationIdFilter.ATTRIBUTE);
        return value instanceof String text ? text : "";
    }
}
