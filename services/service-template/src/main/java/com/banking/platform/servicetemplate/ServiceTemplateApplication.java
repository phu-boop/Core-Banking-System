package com.banking.platform.servicetemplate;

import com.banking.platform.servicetemplate.config.DatabaseConnectionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(DatabaseConnectionProperties.class)
public class ServiceTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceTemplateApplication.class, args);
    }
}
