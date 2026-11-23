package com.banking.platform.account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.banking.platform.account.config.AccountSecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties(AccountSecurityProperties.class)
@OpenAPIDefinition(info = @Info(title = "Account Service", version = "1.0", description = "Account lifecycle APIs"))
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }
}
