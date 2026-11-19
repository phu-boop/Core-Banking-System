package com.banking.platform.branch.application;

import com.banking.platform.branch.domain.BranchPage;
import com.banking.platform.branch.domain.BranchResponse;
import com.banking.platform.branch.domain.BranchStatus;
import com.banking.platform.branch.persistence.BranchRepository;
import com.banking.platform.branch.web.CreateBranchRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BranchService {
    private static final Pattern CODE_PATTERN = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_-]{0,31}");
    private static final int MAX_PAGE_SIZE = 100;

    private final BranchRepository repository;
    private final BranchAuthorizationService authorization;

    public BranchService(BranchRepository repository, BranchAuthorizationService authorization) {
        this.repository = repository;
        this.authorization = authorization;
    }

    @Transactional
    public BranchResponse create(CreateBranchRequest request, Jwt jwt, String correlationId) {
        authorization.requireAdministration(jwt);
        if (request == null || request.code() == null || !CODE_PATTERN.matcher(request.code().trim()).matches()) {
            throw new InvalidBranchException("code must be 1-32 letters, digits, hyphens or underscores");
        }
        if (request.name() == null || request.name().isBlank() || request.name().trim().length() > 120) {
            throw new InvalidBranchException("name is required and must be at most 120 characters");
        }
        String code = request.code().trim().toUpperCase(Locale.ROOT);
        UUID branchId = UUID.randomUUID();
        try {
            BranchResponse branch = repository.create(branchId, code, request.name().trim());
            repository.recordCreateAudit(actor(jwt), branchId, correlationId);
            return branch;
        } catch (DuplicateKeyException exception) {
            throw new DuplicateBranchCodeException(code);
        }
    }

    @Transactional(readOnly = true)
    public BranchResponse get(UUID branchId, Jwt jwt) {
        if (!authorization.canRead(jwt, branchId)) {
            throw new BranchNotFoundException(branchId);
        }
        return repository.findById(branchId)
                .orElseThrow(() -> new BranchNotFoundException(branchId));
    }

    @Transactional(readOnly = true)
    public BranchPage list(BranchStatus status, int page, int size, Jwt jwt) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidPageRequestException();
        }
        List<UUID> visibleBranchIds = authorization.requireListVisibility(jwt);
        return repository.findPage(status, page, size,
                visibleBranchIds == null, visibleBranchIds == null ? List.of() : visibleBranchIds);
    }

    private String actor(Jwt jwt) {
        return jwt.getIssuer() + "/" + jwt.getSubject();
    }

    public static class InvalidBranchException extends RuntimeException {
        public InvalidBranchException(String message) {
            super(message);
        }
    }

    public static class DuplicateBranchCodeException extends RuntimeException {
        public DuplicateBranchCodeException(String code) {
            super("Branch code already exists: " + code);
        }
    }

    public static class BranchNotFoundException extends RuntimeException {
        public BranchNotFoundException(UUID branchId) {
            super("Branch not found: " + branchId);
        }
    }

    public static class InvalidPageRequestException extends RuntimeException {
        public InvalidPageRequestException() {
            super("page must be non-negative and size must be between 1 and 100");
        }
    }
}
