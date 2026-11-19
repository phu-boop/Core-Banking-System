package com.banking.platform.identity.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CorrelationIdFilterTests {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    void propagatesCorrelationIdAndCleansRequestMdc() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("GET");
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "identity-request");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) -> {
            assertThat(MDC.get("correlationId")).isEqualTo("identity-request");
            assertThat(MDC.get("serviceName")).isEqualTo("identity-service");
        });

        assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("identity-request");
        assertThat(MDC.get("correlationId")).isNull();
        assertThat(MDC.get("serviceName")).isNull();
    }
}
