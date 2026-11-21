package com.banking.platform.payment.web;

import com.banking.platform.payment.application.PaymentService;
import com.banking.platform.payment.domain.PaymentOutcomeRequest;
import com.banking.platform.payment.domain.PaymentRecord;
import com.banking.platform.payment.domain.SubmitPaymentRequest;
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
@RequestMapping("/internal/payments")
public class PaymentController {
    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public PaymentRecord submit(@RequestBody SubmitPaymentRequest request,
                                @RequestHeader(name = "Idempotency-Key", required = false) String idempotencyKey,
                                @AuthenticationPrincipal Jwt jwt,
                                HttpServletRequest servletRequest) {
        return service.submit(request, idempotencyKey, jwt.getSubject(),
                correlationId(servletRequest));
    }

    @GetMapping("/{paymentId}")
    public PaymentRecord get(@PathVariable UUID paymentId) {
        return service.get(paymentId);
    }

    @PostMapping("/{paymentId}/outcomes")
    public PaymentRecord outcome(@PathVariable UUID paymentId,
                                 @RequestBody PaymentOutcomeRequest request,
                                 @AuthenticationPrincipal Jwt jwt) {
        return service.recordOutcome(paymentId, new PaymentOutcomeRequest(
                request.outcome(), request.outcomeSource(),
                request.providerReference(), request.correlationId()));
    }

    private String correlationId(HttpServletRequest request) {
        Object value = request.getAttribute("X-Correlation-Id");
        return value instanceof String text ? text : "";
    }
}
