package com.banking.platform.identity.web;

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
