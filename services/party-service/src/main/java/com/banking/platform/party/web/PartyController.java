package com.banking.platform.party.web;

import com.banking.platform.party.application.PartyService;
import com.banking.platform.party.domain.PartyResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customers")
public class PartyController {
    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('party:create')")
    public ResponseEntity<PartyResponse> create(
            @RequestBody CreatePartyRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest servletRequest) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String correlationId = correlationId(servletRequest);
        PartyResponse response = partyService.create(request, actor(jwt),
                idempotencyKey, correlationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{partyId}")
    @PreAuthorize("hasAuthority('party:read')")
    public PartyResponse get(@PathVariable UUID partyId) {
        return partyService.get(partyId);
    }

    @PatchMapping("/{partyId}")
    @PreAuthorize("hasAuthority('party:update')")
    public PartyResponse update(@PathVariable UUID partyId,
                                @RequestBody UpdatePartyRequest request,
                                @AuthenticationPrincipal Jwt jwt,
                                HttpServletRequest servletRequest) {
        return partyService.update(partyId, request, actor(jwt),
                correlationId(servletRequest));
    }

    private String actor(Jwt jwt) {
        return jwt.getIssuer() + "/" + jwt.getSubject();
    }

    private String correlationId(HttpServletRequest request) {
        String value = request.getHeader("X-Correlation-ID");
        return value == null || value.isBlank() ? UUID.randomUUID().toString() : value;
    }
}
