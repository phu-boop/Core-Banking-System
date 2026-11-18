package com.banking.platform.servicetemplate.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String HEADER_NAME = "X-Correlation-Id";
    private static final String MDC_KEY = "correlationId";
    private static final String MDC_SERVICE_NAME = "serviceName";
    private static final String MDC_METHOD = "httpMethod";
    private static final String MDC_PATH = "httpPath";
    private static final String MDC_STATUS = "httpStatus";
    private static final String MDC_DURATION = "durationMs";
    private static final String SERVICE_NAME = "service-template";
    private static final Pattern VALID_CORRELATION_ID = Pattern.compile("[A-Za-z0-9._-]{1,64}");
    private static final Logger LOGGER = LoggerFactory.getLogger(CorrelationIdFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String correlationId = request.getHeader(HEADER_NAME);
        if (correlationId != null && !VALID_CORRELATION_ID.matcher(correlationId).matches()) {
            putRequestMdc(request, correlationId, HttpServletResponse.SC_BAD_REQUEST, 0);
            LOGGER.warn("Rejected malformed correlation ID");
            clearRequestMdc();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid X-Correlation-Id header");
            return;
        }

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        Map<String, String> previousMdc = snapshotRequestMdc();
        MDC.put(MDC_KEY, correlationId);
        MDC.put(MDC_SERVICE_NAME, SERVICE_NAME);
        MDC.put(MDC_METHOD, request.getMethod());
        MDC.put(MDC_PATH, request.getRequestURI());
        response.setHeader(HEADER_NAME, correlationId);
        long startedAt = System.nanoTime();
        try {
            LOGGER.info("HTTP request received");
            filterChain.doFilter(request, response);
        } catch (ServletException | IOException exception) {
            MDC.put(MDC_STATUS, String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
            LOGGER.error("HTTP request failed", exception);
            throw exception;
        } catch (RuntimeException exception) {
            MDC.put(MDC_STATUS, String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
            LOGGER.error("HTTP request failed", exception);
            throw exception;
        } finally {
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            if (MDC.get(MDC_STATUS) == null) {
                MDC.put(MDC_STATUS, String.valueOf(response.getStatus()));
            }
            MDC.put(MDC_DURATION, String.valueOf(durationMs));
            LOGGER.info("HTTP request completed");
            restoreRequestMdc(previousMdc);
        }
    }

    private void putRequestMdc(
            HttpServletRequest request,
            String correlationId,
            int status,
            long durationMs) {
        MDC.put(MDC_KEY, correlationId);
        MDC.put(MDC_SERVICE_NAME, SERVICE_NAME);
        MDC.put(MDC_METHOD, request.getMethod());
        MDC.put(MDC_PATH, request.getRequestURI());
        MDC.put(MDC_STATUS, String.valueOf(status));
        MDC.put(MDC_DURATION, String.valueOf(durationMs));
    }

    private Map<String, String> snapshotRequestMdc() {
        Map<String, String> previous = new HashMap<>();
        for (String key : new String[]{
                MDC_KEY, MDC_SERVICE_NAME, MDC_METHOD, MDC_PATH, MDC_STATUS, MDC_DURATION}) {
            String value = MDC.get(key);
            if (value != null) {
                previous.put(key, value);
            }
        }
        return previous;
    }

    private void restoreRequestMdc(Map<String, String> previous) {
        for (String key : new String[]{
                MDC_KEY, MDC_SERVICE_NAME, MDC_METHOD, MDC_PATH, MDC_STATUS, MDC_DURATION}) {
            String value = previous.get(key);
            if (value == null) {
                MDC.remove(key);
            } else {
                MDC.put(key, value);
            }
        }
    }

    private void clearRequestMdc() {
        restoreRequestMdc(Map.of());
    }
}
