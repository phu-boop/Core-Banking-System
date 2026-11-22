package com.banking.platform.audit.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "audit.security")
public record AuditSecurityProperties(String issuerUri, String jwkSetUri, String audience) {}
