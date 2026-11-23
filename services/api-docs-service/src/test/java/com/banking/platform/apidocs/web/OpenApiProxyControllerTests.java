package com.banking.platform.apidocs.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.banking.platform.apidocs.config.ApiDocsProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

class OpenApiProxyControllerTests {
    @Test
    void unknownServiceReturnsNotFound() {
        OpenApiProxyController controller = new OpenApiProxyController(
                new ApiDocsProperties(Map.of()), new RestTemplate());

        assertThrows(ResponseStatusException.class,
                () -> controller.specification("unknown"));
    }

    @Test
    void validDocumentShapeCanBeReturned() throws Exception {
        ApiDocsProperties properties = new ApiDocsProperties(
                Map.of("identity", "http://localhost/v3/api-docs"));
        JsonNode document = new ObjectMapper().readTree(
                "{\"openapi\":\"3.1.0\",\"info\":{\"title\":\"Identity\",\"version\":\"1\"},\"paths\":{}}");

        assertEquals("3.1.0", document.get("openapi").asText());
        assertEquals("Identity", document.get("info").get("title").asText());
    }
}
