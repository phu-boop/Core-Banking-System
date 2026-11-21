package com.banking.platform.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "payment.security")
public record PaymentSecurityProperties(String issuerUri, String jwkSetUri, String audience) {}
