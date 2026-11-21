package com.banking.platform.corporate.web;

import com.banking.platform.corporate.application.OrganizationService.DuplicateOrganizationPartyException;
import com.banking.platform.corporate.application.OrganizationService.InvalidOrganizationException;
import com.banking.platform.corporate.application.OrganizationService.OrganizationNotFoundException;
import com.banking.platform.corporate.application.ApprovalPolicyService.ApprovalPolicyConflictException;
import com.banking.platform.corporate.application.ApprovalPolicyService.ApprovalPolicyNotFoundException;
import com.banking.platform.corporate.application.ApprovalPolicyService.InvalidApprovalPolicyException;
import com.banking.platform.corporate.application.ApprovalRequestConflictException;
import com.banking.platform.corporate.application.ApprovalRequestNotFoundException;
import com.banking.platform.corporate.application.ApprovalRequestValidationException;
import com.banking.platform.corporate.application.IneligibleApproverException;
import com.banking.platform.corporate.application.MakerCheckerException;
import com.banking.platform.corporate.application.MembershipService.MembershipNotFoundException;
import com.banking.platform.corporate.application.MembershipService.MembershipValidationException;
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

    @ExceptionHandler(InvalidApprovalPolicyException.class)
    public ResponseEntity<Map<String, Object>> invalidApprovalPolicy(
            InvalidApprovalPolicyException exception, HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_APPROVAL_POLICY",
                exception.getMessage(), request);
    }

    @ExceptionHandler(ApprovalPolicyConflictException.class)
    public ResponseEntity<Map<String, Object>> approvalPolicyConflict(
            ApprovalPolicyConflictException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "APPROVAL_POLICY_CONFLICT",
                exception.getMessage(), request);
    }

    @ExceptionHandler(ApprovalPolicyNotFoundException.class)
    public ResponseEntity<Map<String, Object>> approvalPolicyNotFound(
            ApprovalPolicyNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "APPROVAL_POLICY_NOT_FOUND",
                exception.getMessage(), request);
    }

    @ExceptionHandler(ApprovalRequestValidationException.class)
    public ResponseEntity<Map<String, Object>> invalidApprovalRequest(
            ApprovalRequestValidationException exception, HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_APPROVAL_REQUEST",
                exception.getMessage(), request);
    }

    @ExceptionHandler(ApprovalRequestConflictException.class)
    public ResponseEntity<Map<String, Object>> approvalRequestConflict(
            ApprovalRequestConflictException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, "APPROVAL_REQUEST_CONFLICT",
                exception.getMessage(), request);
    }

    @ExceptionHandler(ApprovalRequestNotFoundException.class)
    public ResponseEntity<Map<String, Object>> approvalRequestNotFound(
            ApprovalRequestNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "APPROVAL_REQUEST_NOT_FOUND",
                exception.getMessage(), request);
    }

    @ExceptionHandler(MakerCheckerException.class)
    public ResponseEntity<Map<String, Object>> makerChecker(
            MakerCheckerException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "MAKER_CHECKER_VIOLATION",
                exception.getMessage(), request);
    }

    @ExceptionHandler(IneligibleApproverException.class)
    public ResponseEntity<Map<String, Object>> ineligibleApprover(
            IneligibleApproverException exception, HttpServletRequest request) {
        return error(HttpStatus.FORBIDDEN, "APPROVER_NOT_ELIGIBLE",
                exception.getMessage(), request);
    }

    @ExceptionHandler(MembershipValidationException.class)
    public ResponseEntity<Map<String, Object>> invalidMembership(
            MembershipValidationException exception, HttpServletRequest request) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, "INVALID_MEMBERSHIP",
                exception.getMessage(), request);
    }

    @ExceptionHandler(MembershipNotFoundException.class)
    public ResponseEntity<Map<String, Object>> membershipNotFound(
            MembershipNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, "MEMBERSHIP_NOT_FOUND",
                exception.getMessage(), request);
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String code,
                                                      String message, HttpServletRequest request) {
        Object attribute = request.getAttribute(CorrelationIdFilter.CORRELATION_ID_ATTRIBUTE);
        String correlationId = attribute instanceof String value ? value : "";
        return ResponseEntity.status(status).body(Map.of(
                "code", code,
                "message", message,
                "correlationId", correlationId,
                "details", List.of()));
    }
}
