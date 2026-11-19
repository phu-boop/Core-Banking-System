package com.banking.platform.party.web;

import com.banking.platform.party.application.PartyService.InvalidPartyException;
import com.banking.platform.party.application.PartyService.PartyNotFoundException;
import com.banking.platform.party.application.PartyService.StalePartyVersionException;
import com.banking.platform.party.application.PartyService.IdempotencyConflictException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PartyExceptionHandler {
    @ExceptionHandler(PartyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(PartyNotFoundException exception,
                                                        HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "PARTY_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidPartyException.class)
    public ResponseEntity<Map<String, Object>> invalid(InvalidPartyException exception,
                                                       HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_PARTY", exception.getMessage(), request);
    }

    @ExceptionHandler(StalePartyVersionException.class)
    public ResponseEntity<Map<String, Object>> stale(StalePartyVersionException exception,
                                                     HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "STALE_PARTY_VERSION", exception.getMessage(), request);
    }

    @ExceptionHandler(IdempotencyConflictException.class)
    public ResponseEntity<Map<String, Object>> idempotencyConflict(IdempotencyConflictException exception,
                                                          HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT", exception.getMessage(), request);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String code,
                                                      String message, HttpServletRequest request) {
        return ResponseEntity.status(status).body(Map.of(
                "code", code,
                "message", message,
                "correlationId",
                request.getHeader("X-Correlation-ID") == null
                        ? "" : request.getHeader("X-Correlation-ID"),
                "details", List.of()));
    }
}
