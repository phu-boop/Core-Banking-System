package com.banking.platform.ledger.web;

import com.banking.platform.ledger.application.LedgerService;
import com.banking.platform.ledger.domain.BalanceResponse;
import com.banking.platform.ledger.domain.JournalRequest;
import com.banking.platform.ledger.domain.JournalResponse;
import com.banking.platform.ledger.domain.ReversalRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/internal/ledger")
public class LedgerController {
    private final LedgerService service;

    public LedgerController(LedgerService service) {
        this.service = service;
    }

    @PostMapping("/journals")
    @ResponseStatus(HttpStatus.CREATED)
    public JournalResponse post(@RequestBody JournalRequest request,
                                @RequestHeader(value = "X-Actor", defaultValue = "unknown") String actor,
                                @RequestHeader(value = "X-Correlation-Id", defaultValue = "none") String correlationId) {
        return service.post(request, actor, correlationId);
    }

    @PostMapping("/journals/{journalId}/reversals")
    public JournalResponse reverse(@PathVariable UUID journalId,
                                   @RequestBody ReversalRequest request,
                                   @RequestHeader(value = "X-Actor", defaultValue = "unknown") String actor,
                                   @RequestHeader(value = "X-Correlation-Id", defaultValue = "none") String correlationId) {
        return service.reverse(journalId, request, actor, correlationId);
    }

    @GetMapping("/journals/{journalId}")
    public JournalResponse get(@PathVariable UUID journalId) {
        return service.get(journalId);
    }

    @GetMapping("/accounts/{accountId}/balance")
    public BalanceResponse balance(@PathVariable UUID accountId) {
        return service.balance(accountId);
    }
}
