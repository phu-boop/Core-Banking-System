package com.banking.platform.branch;

import com.banking.platform.branch.config.BranchSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(BranchSecurityProperties.class)
public class BranchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BranchServiceApplication.class, args);
    }
}
