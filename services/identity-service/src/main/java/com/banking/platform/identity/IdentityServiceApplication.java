package com.banking.platform.identity;

import com.banking.platform.identity.config.IdentityDatabaseProperties;
import com.banking.platform.identity.config.IdentitySecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({IdentityDatabaseProperties.class, IdentitySecurityProperties.class})
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }
}
