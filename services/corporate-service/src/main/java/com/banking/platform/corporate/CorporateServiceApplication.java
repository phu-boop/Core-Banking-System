package com.banking.platform.corporate;

import com.banking.platform.corporate.config.CorporateSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CorporateSecurityProperties.class)
public class CorporateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CorporateServiceApplication.class, args);
    }
}
