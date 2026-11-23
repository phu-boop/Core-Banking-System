package com.banking.platform.account.web;

import com.banking.platform.account.application.AccountService;
import com.banking.platform.account.integration.IdentityServiceUnavailableException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AccountExceptionHandler {
    @ExceptionHandler(AccountService.InvalidAccountException.class)
    public ResponseEntity<Map<String, String>> invalid(AccountService.InvalidAccountException exception) {
        return ResponseEntity.badRequest().body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(AccountService.DuplicateAccountException.class)
    public ResponseEntity<Map<String, String>> duplicate(AccountService.DuplicateAccountException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Account number already exists"));
    }

    @ExceptionHandler(AccountService.AccountNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(AccountService.AccountNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(AccountService.InvalidAccountTransitionException.class)
    public ResponseEntity<Map<String, String>> transition(AccountService.InvalidAccountTransitionException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", exception.getMessage()));
    }

    @ExceptionHandler(IdentityServiceUnavailableException.class)
    public ResponseEntity<Map<String, String>> identityUnavailable(
            IdentityServiceUnavailableException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", "Identity Service is unavailable"));
    }
}
