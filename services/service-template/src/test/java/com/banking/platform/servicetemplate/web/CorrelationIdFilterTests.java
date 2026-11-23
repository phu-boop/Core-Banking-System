package com.banking.platform.servicetemplate.web;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.slf4j.LoggerFactory;
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
        request.setMethod("GET");
        request.addHeader(CorrelationIdFilter.HEADER_NAME, "inner-request");
        MockHttpServletResponse response = new MockHttpServletResponse();
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        Logger logger = (Logger) LoggerFactory.getLogger(CorrelationIdFilter.class);
        appender.start();
        logger.addAppender(appender);

        try {
            filter.doFilter(request, response, (servletRequest, servletResponse) ->
                    assertThat(MDC.get("correlationId")).isEqualTo("inner-request"));
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }

        assertThat(response.getHeader(CorrelationIdFilter.HEADER_NAME)).isEqualTo("inner-request");
        assertThat(MDC.get("correlationId")).isEqualTo("outer-request");
        assertThat(appender.list)
                .hasSize(2)
                .allSatisfy(event -> {
                    assertThat(event.getMDCPropertyMap().get("correlationId")).isEqualTo("inner-request");
                    assertThat(event.getMDCPropertyMap().get("serviceName")).isEqualTo("service-template");
                    assertThat(event.getMDCPropertyMap().get("httpMethod")).isEqualTo("GET");
                });
        assertThat(appender.list)
                .filteredOn(event -> "HTTP request completed".equals(event.getMessage()))
                .singleElement()
                .satisfies(event -> {
                    assertThat(event.getMDCPropertyMap().get("httpStatus")).isNotBlank();
                    assertThat(event.getMDCPropertyMap().get("durationMs")).isNotBlank();
                });
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

    @Test
    void doesNotLogAuthorizationHeaderOrPasswordParameter() throws Exception {
        String bearerToken = "test-bearer-token";
        String password = "test-password";
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setMethod("POST");
        request.addHeader("Authorization", "Bearer " + bearerToken);
        request.setParameter("password", password);
        MockHttpServletResponse response = new MockHttpServletResponse();
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        Logger logger = (Logger) LoggerFactory.getLogger(CorrelationIdFilter.class);
        appender.start();
        logger.addAppender(appender);

        try {
            filter.doFilter(request, response, (servletRequest, servletResponse) -> {});
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }

        assertThat(appender.list).isNotEmpty().allSatisfy(event -> {
            String loggedValues = event.getFormattedMessage() + event.getMDCPropertyMap();
            assertThat(loggedValues).doesNotContain(bearerToken, password);
        });
    }
}
