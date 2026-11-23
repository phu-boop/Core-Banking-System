package com.banking.platform.party;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.banking.platform.party.config.PartySecurityProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Party Service", version = "1.0", description = "Party and customer master-data APIs"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@EnableConfigurationProperties(PartySecurityProperties.class)
public class PartyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PartyServiceApplication.class, args);
    }
}
