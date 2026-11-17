package com.banking.platform.servicetemplate.web;

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
    void restoresPreviousMdcValueAfterRequest() throws Exception {
        MDC.put("correlationId", "outer-request");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "inner-request");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                assertThat(MDC.get("correlationId")).isEqualTo("inner-request"));

        assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("inner-request");
        assertThat(MDC.get("correlationId")).isEqualTo("outer-request");
    }

    @Test
    void clearsMdcValueAfterRequestWhenThereWasNoPreviousValue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "request-123");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                assertThat(MDC.get("correlationId")).isEqualTo("request-123"));

        assertThat(MDC.get("correlationId")).isNull();
    }
}
