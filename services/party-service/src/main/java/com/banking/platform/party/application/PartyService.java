package com.banking.platform.party.application;

import com.banking.platform.party.domain.PartyResponse;
import com.banking.platform.party.persistence.PartyRepository;
import com.banking.platform.party.web.CreatePartyRequest;
import com.banking.platform.party.web.UpdatePartyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;
import java.sql.Timestamp;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartyService {
    private final PartyRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public PartyService(PartyRepository repository, JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PartyResponse create(CreatePartyRequest request, String actor, String idempotencyKey,
                                String correlationId) {
        validateCreate(request);
        String hash = payloadHash(request);
        var existing = jdbcTemplate.query("""
                SELECT payload_hash, response_body, expires_at
                FROM party_schema.idempotency_record
                WHERE actor = ? AND operation = 'PARTY_CREATE'
                  AND business_scope = 'CUSTOMER' AND idempotency_key = ?
                """, (rs, rowNum) -> new IdempotencyRecord(
                        rs.getString("payload_hash"),
                        rs.getString("response_body"),
                        rs.getTimestamp("expires_at").toInstant()), actor, idempotencyKey);
        if (!existing.isEmpty()) {
            var record = existing.getFirst();
            if (record.expiresAt().isBefore(Instant.now())) {
                // Continue and replace this expired record below.
            } else if (!record.payloadHash().equals(hash)) {
                throw new IdempotencyConflictException();
            } else {
                try {
                    return objectMapper.readValue(record.responseBody(), PartyResponse.class);
                } catch (Exception exception) {
                    throw new IllegalStateException("Cannot replay party create response", exception);
                }
            }
        }
        UUID partyId = UUID.randomUUID();
        PartyResponse party = repository.create(partyId, request);
        repository.recordAudit(actor, "PARTY_CREATE", partyId.toString(), "SUCCESS",
                correlationId, partyId, request.managingBranchId());
        try {
            jdbcTemplate.update("""
                    INSERT INTO party_schema.idempotency_record
                        (actor, operation, business_scope, idempotency_key, payload_hash,
                         status_code, response_body, expires_at)
                    VALUES (?, 'PARTY_CREATE', 'CUSTOMER', ?, ?, 201, ?, ?)
                    ON CONFLICT (actor, operation, business_scope, idempotency_key)
                    DO UPDATE SET payload_hash = EXCLUDED.payload_hash,
                                  status_code = EXCLUDED.status_code,
                                  response_body = EXCLUDED.response_body,
                                  expires_at = EXCLUDED.expires_at,
                                  created_at = CURRENT_TIMESTAMP
                    """, actor, idempotencyKey, hash,
                    objectMapper.writeValueAsString(party),
                    Timestamp.from(Instant.now().plusSeconds(24 * 60 * 60)));
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot store party idempotency result", exception);
        }
        return party;
    }

    @Transactional(readOnly = true)
    public PartyResponse get(UUID partyId) {
        return repository.findById(partyId)
                .orElseThrow(() -> new PartyNotFoundException(partyId));
    }

    @Transactional
    public PartyResponse update(UUID partyId, UpdatePartyRequest request, String actor,
                               String correlationId) {
        if (request.displayName() == null || request.displayName().isBlank()) {
            throw new InvalidPartyException("displayName is required");
        }
        int updated = repository.update(partyId, request.version(), request);
        if (updated == 0) {
            if (repository.findById(partyId).isEmpty()) {
                throw new PartyNotFoundException(partyId);
            }
            throw new StalePartyVersionException(partyId);
        }
        PartyResponse party = repository.findById(partyId).orElseThrow();
        repository.recordAudit(actor, "PARTY_UPDATE", partyId.toString(), "SUCCESS",
                correlationId, partyId, request.managingBranchId());
        return party;
    }

    public String payloadHash(Object payload) {
        try {
            byte[] bytes = objectMapper.writeValueAsString(payload).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (Exception exception) {
            throw new IllegalStateException("Cannot hash idempotency payload", exception);
        }
    }

    private void validateCreate(CreatePartyRequest request) {
        if (request == null || request.partyType() == null) {
            throw new InvalidPartyException("partyType is required");
        }
        if (request.displayName() == null || request.displayName().isBlank()) {
            throw new InvalidPartyException("displayName is required");
        }
        if (request.partyType().name().equals("INDIVIDUAL")
                && request.individualProfile() != null
                && request.individualProfile().nationalIdHash() != null
                && request.individualProfile().nationalIdHash().length() < 32) {
            throw new InvalidPartyException("nationalIdHash must contain at least 32 characters");
        }
    }

    public static class PartyNotFoundException extends RuntimeException {
        public PartyNotFoundException(UUID partyId) {
            super("Party not found: " + partyId);
        }
    }

    public static class InvalidPartyException extends RuntimeException {
        public InvalidPartyException(String message) {
            super(message);
        }
    }

    public static class StalePartyVersionException extends RuntimeException {
        public StalePartyVersionException(UUID partyId) {
            super("Stale party version: " + partyId);
        }
    }

    public static class IdempotencyConflictException extends RuntimeException {
        public IdempotencyConflictException() {
            super("Idempotency key was reused with a different payload");
        }
    }

    private record IdempotencyRecord(String payloadHash, String responseBody, Instant expiresAt) {}
}
