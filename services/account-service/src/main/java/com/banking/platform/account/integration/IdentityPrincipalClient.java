package com.banking.platform.account.integration;

import com.banking.platform.account.config.IdentityClientProperties;
import java.time.Duration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestClientException;

@Component
public class IdentityPrincipalClient {

    private final RestClient restClient;

    public IdentityPrincipalClient(
            RestClient.Builder builder,
            IdentityClientProperties properties) {
        this(buildRestClient(builder, properties));
    }

    private static RestClient buildRestClient(
            RestClient.Builder builder,
            IdentityClientProperties properties) {
        var httpClient = java.net.http.HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
        var requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        return builder.baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();
    }

    IdentityPrincipalClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public IdentityPrincipal currentPrincipal(String accessToken) {
        try {
            IdentityPrincipal principal = restClient.get()
                    .uri("/v1/identity/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .body(IdentityPrincipal.class);
            if (principal == null || principal.userId() == null || principal.grants() == null) {
                throw new IdentityServiceUnavailableException();
            }
            return principal;
        } catch (RestClientResponseException exception) {
            if (exception.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new AccessDeniedException("Identity mapping is not active");
            }
            throw new IdentityServiceUnavailableException(exception);
        } catch (RestClientException exception) {
            throw new IdentityServiceUnavailableException(exception);
        }
    }
}
