package com.banking.platform.corporate.web;

import com.banking.platform.corporate.application.OrganizationService.DuplicateOrganizationPartyException;
import com.banking.platform.corporate.application.OrganizationService.InvalidOrganizationException;
import com.banking.platform.corporate.application.OrganizationService.OrganizationNotFoundException;
import com.banking.platform.corporate.integration.OidcPartyReferenceClient.PartyServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CorporateExceptionHandler {
    @ExceptionHandler(InvalidOrganizationException.class)
    public ResponseEntity<Map<String, Object>> invalid(InvalidOrganizationException exception,
                                                       HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_ORGANIZATION", exception.getMessage(), request);
    }

    @ExceptionHandler(DuplicateOrganizationPartyException.class)
    public ResponseEntity<Map<String, Object>> duplicate(DuplicateOrganizationPartyException exception,
                                                         HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "ORGANIZATION_PARTY_ALREADY_LINKED", exception.getMessage(), request);
    }

    @ExceptionHandler(OrganizationNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(OrganizationNotFoundException exception,
                                                        HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "ORGANIZATION_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(PartyServiceUnavailableException.class)
    public ResponseEntity<Map<String, Object>> partyUnavailable(PartyServiceUnavailableException exception,
                                                                HttpServletRequest request) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "PARTY_SERVICE_UNAVAILABLE",
                "Party reference validation is temporarily unavailable", request);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String code,
                                                      String message, HttpServletRequest request) {
        String correlationId = request.getHeader("X-Correlation-Id");
        return ResponseEntity.status(status).body(Map.of(
                "code", code,
                "message", message,
                "correlationId", correlationId == null ? "" : correlationId,
                "details", List.of()));
    }
}
