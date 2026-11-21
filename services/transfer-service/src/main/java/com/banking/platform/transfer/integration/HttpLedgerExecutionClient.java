package com.banking.platform.transfer.integration;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpLedgerExecutionClient implements LedgerExecutionClient {
    private final RestTemplate restTemplate;
    private final String ledgerBaseUrl;

    public HttpLedgerExecutionClient(
            RestTemplate restTemplate,
            @Value("${transfer.ledger.base-url:http://localhost:8086}") String ledgerBaseUrl) {
        this.restTemplate = restTemplate;
        this.ledgerBaseUrl = ledgerBaseUrl;
    }

    @Override
    public UUID postInternalJournal(UUID transactionId, UUID sourceAccountId,
                                     UUID destinationAccountId, BigDecimal amount,
                                     String operationKey, String correlationId) {
        Map<String, Object> request = Map.of(
                "transactionId", transactionId,
                "operationKey", operationKey,
                "currency", "VND",
                "entries", List.of(
                        Map.of("accountId", sourceAccountId, "side", "DEBIT", "amount", amount),
                        Map.of("accountId", destinationAccountId, "side", "CREDIT", "amount", amount)));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Correlation-Id", correlationId);
        LedgerJournalResponse response = restTemplate.postForObject(
                ledgerBaseUrl + "/internal/ledger/journals",
                new HttpEntity<>(request, headers),
                LedgerJournalResponse.class);
        if (response == null || response.journalId() == null) {
            throw new IllegalStateException("Ledger returned no journal");
        }
        return response.journalId();
    }

    public record LedgerJournalResponse(UUID journalId, String status) {}
}
