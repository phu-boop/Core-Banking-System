package com.banking.platform.branch.web;

import com.banking.platform.branch.application.BranchService;
import com.banking.platform.branch.domain.BranchPage;
import com.banking.platform.branch.domain.BranchResponse;
import com.banking.platform.branch.domain.BranchStatus;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/branches")
public class BranchController {
    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping
    public ResponseEntity<BranchResponse> create(@RequestBody CreateBranchRequest request,
                                                 @AuthenticationPrincipal Jwt jwt,
                                                 HttpServletRequest servletRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                branchService.create(request, jwt, servletRequest.getHeader("X-Correlation-Id")));
    }

    @GetMapping("/{branchId}")
    public BranchResponse get(@PathVariable UUID branchId, @AuthenticationPrincipal Jwt jwt) {
        return branchService.get(branchId, jwt);
    }

    @GetMapping
    public BranchPage list(@RequestParam(required = false) BranchStatus status,
                           @RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "20") int size,
                           @AuthenticationPrincipal Jwt jwt) {
        return branchService.list(status, page, size, jwt);
    }
}
