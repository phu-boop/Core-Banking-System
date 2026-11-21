package com.banking.platform.ledger.web;

import com.banking.platform.ledger.application.LedgerService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class LedgerExceptionHandler {
    @ExceptionHandler(LedgerService.InvalidJournalException.class)
    public ResponseEntity<Map<String, String>> invalid(LedgerService.InvalidJournalException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(LedgerService.UnbalancedJournalException.class)
    public ResponseEntity<Map<String, String>> unbalanced(LedgerService.UnbalancedJournalException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", "Debit and credit totals must be equal"));
    }

    @ExceptionHandler(LedgerService.JournalNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(LedgerService.JournalNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(LedgerService.JournalAlreadyReversedException.class)
    public ResponseEntity<Map<String, String>> reversed(LedgerService.JournalAlreadyReversedException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }
}
