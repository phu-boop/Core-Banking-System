package com.banking.platform.transfer.web;

import com.banking.platform.transfer.application.TransferExceptions;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TransferExceptionHandler {
    @ExceptionHandler(TransferExceptions.NotFoundException.class)
    ResponseEntity<Map<String, String>> notFound(TransferExceptions.NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(TransferExceptions.ConflictException.class)
    ResponseEntity<Map<String, String>> conflict(TransferExceptions.ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    ResponseEntity<Map<String, String>> unsupported(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", ex.getMessage()));
    }
}
