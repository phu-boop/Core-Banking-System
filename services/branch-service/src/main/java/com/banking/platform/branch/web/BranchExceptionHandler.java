package com.banking.platform.branch.web;

import com.banking.platform.branch.application.BranchService.BranchNotFoundException;
import com.banking.platform.branch.application.BranchService.DuplicateBranchCodeException;
import com.banking.platform.branch.application.BranchService.InvalidBranchException;
import com.banking.platform.branch.application.BranchService.InvalidPageRequestException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BranchExceptionHandler {
    @ExceptionHandler(BranchNotFoundException.class)
    public ResponseEntity<Map<String, Object>> notFound(BranchNotFoundException exception,
                                                        HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "BRANCH_NOT_FOUND", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidBranchException.class)
    public ResponseEntity<Map<String, Object>> invalid(InvalidBranchException exception,
                                                       HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_BRANCH", exception.getMessage(), request);
    }

    @ExceptionHandler(DuplicateBranchCodeException.class)
    public ResponseEntity<Map<String, Object>> duplicate(DuplicateBranchCodeException exception,
                                                         HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "BRANCH_CODE_EXISTS", exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidPageRequestException.class)
    public ResponseEntity<Map<String, Object>> invalidPage(InvalidPageRequestException exception,
                                                           HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_PAGE", exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> invalidFilter(
            MethodArgumentTypeMismatchException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, "INVALID_FILTER",
                "Invalid value for " + exception.getName(), request);
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
