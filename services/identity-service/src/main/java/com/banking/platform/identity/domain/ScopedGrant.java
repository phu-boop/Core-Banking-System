package com.banking.platform.identity.domain;

public record ScopedGrant(String permission, ScopeType scopeType, String scopeId) {}
