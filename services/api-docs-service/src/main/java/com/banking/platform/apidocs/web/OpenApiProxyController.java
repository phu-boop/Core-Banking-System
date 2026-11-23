package com.banking.platform.apidocs.web;

import com.banking.platform.apidocs.config.ApiDocsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class OpenApiProxyController {
    private final ApiDocsProperties properties;
    private final RestTemplate restTemplate;

    public OpenApiProxyController(ApiDocsProperties properties,
                                  RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/specs/{serviceName}")
    public JsonNode specification(@PathVariable String serviceName) {
        Map<String, String> services = properties.services();
        String url = services == null ? null : services.get(serviceName);
        if (url == null || url.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "OpenAPI specification is not configured");
        }
        try {
            JsonNode document = restTemplate.getForObject(url, JsonNode.class);
            if (document == null || !document.isObject()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY, "OpenAPI specification is invalid");
            }
            return document;
        } catch (RestClientException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "OpenAPI specification is unavailable",
                    exception);
        }
    }
}
