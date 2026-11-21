package com.banking.platform.transfer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "transfer.security")
public record TransferSecurityProperties(
        String issuerUri,
        String jwkSetUri,
        String audience) {}
