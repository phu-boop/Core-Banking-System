package com.banking.platform.account.web;

import com.banking.platform.account.application.AccountService;
import com.banking.platform.account.application.AccountAuthorizationService;
import com.banking.platform.account.domain.AccountResponse;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/v1/accounts")
public class AccountController {
    private final AccountService service;
    private final AccountAuthorizationService authorization;

    public AccountController(AccountService service,
                             AccountAuthorizationService authorization) {
        this.service = service;
        this.authorization = authorization;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@RequestBody CreateAccountRequest request,
                                  @RequestHeader(value = "X-Actor", defaultValue = "unknown") String actor,
                                  @RequestHeader(value = "X-Correlation-Id", defaultValue = "none") String correlationId,
                                  @AuthenticationPrincipal Jwt jwt) {
        authorization.requireCreate(jwt);
        return service.create(request, actor, correlationId);
    }

    @GetMapping("/{accountId}")
    public AccountResponse get(@PathVariable UUID accountId,
                               @AuthenticationPrincipal Jwt jwt) {
        AccountResponse account = service.get(accountId);
        authorization.requireRead(jwt, account);
        return account;
    }

    @PostMapping("/{accountId}/block")
    public AccountResponse block(@PathVariable UUID accountId,
                                 @RequestBody(required = false) StateChangeRequest request,
                                 @RequestHeader(value = "X-Actor", defaultValue = "unknown") String actor,
                                 @RequestHeader(value = "X-Correlation-Id", defaultValue = "none") String correlationId,
                                 @AuthenticationPrincipal Jwt jwt) {
        AccountResponse account = service.get(accountId);
        authorization.requireManage(jwt, account);
        return service.block(accountId, request, actor, correlationId);
    }

    @PostMapping("/{accountId}/unblock")
    public AccountResponse unblock(@PathVariable UUID accountId,
                                   @RequestBody(required = false) StateChangeRequest request,
                                   @RequestHeader(value = "X-Actor", defaultValue = "unknown") String actor,
                                   @RequestHeader(value = "X-Correlation-Id", defaultValue = "none") String correlationId,
                                   @AuthenticationPrincipal Jwt jwt) {
        AccountResponse account = service.get(accountId);
        authorization.requireManage(jwt, account);
        return service.unblock(accountId, request, actor, correlationId);
    }
}
