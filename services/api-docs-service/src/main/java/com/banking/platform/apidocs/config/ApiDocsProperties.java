package com.banking.platform.apidocs.config;

import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "api-docs")
public record ApiDocsProperties(Map<String, String> services) {}
