package com.banking.platform.corporate;

import com.banking.platform.corporate.config.CorporateSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Corporate Service", version = "1.0", description = "Organization, membership, mandate and approval APIs"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@EnableConfigurationProperties(CorporateSecurityProperties.class)
public class CorporateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CorporateServiceApplication.class, args);
    }
}
