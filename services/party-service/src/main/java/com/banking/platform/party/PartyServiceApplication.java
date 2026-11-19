package com.banking.platform.party;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.banking.platform.party.config.PartySecurityProperties;

@SpringBootApplication
@EnableConfigurationProperties(PartySecurityProperties.class)
public class PartyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PartyServiceApplication.class, args);
    }
}
