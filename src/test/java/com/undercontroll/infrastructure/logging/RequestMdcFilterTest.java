package com.undercontroll.infrastructure.logging;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestMdcFilterTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    @DisplayName("reuses X-Request-Id and echoes it on the response")
    void reusesRequestId() throws ServletException, IOException {
        RequestMdcFilter filter = new RequestMdcFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/api/orders");
        request.addHeader(RequestMdcFilter.REQUEST_ID_HEADER, "abc-123");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> seen = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> seen.set(MDC.get(MdcKeys.CORRELATION_ID)));

        assertEquals("abc-123", seen.get());
        assertEquals("abc-123", response.getHeader(RequestMdcFilter.REQUEST_ID_HEADER));
        assertNull(MDC.get(MdcKeys.CORRELATION_ID));
    }

    @Test
    @DisplayName("falls back to X-Correlation-Id when X-Request-Id is missing")
    void usesCorrelationHeader() throws ServletException, IOException {
        RequestMdcFilter filter = new RequestMdcFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/api/orders");
        request.addHeader(RequestMdcFilter.CORRELATION_ID_HEADER, "corr-9");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> seen = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> seen.set(MDC.get(MdcKeys.CORRELATION_ID)));

        assertEquals("corr-9", seen.get());
        assertEquals("corr-9", response.getHeader(RequestMdcFilter.REQUEST_ID_HEADER));
    }

    @Test
    @DisplayName("generates a correlation id when no header is present")
    void generatesId() throws ServletException, IOException {
        RequestMdcFilter filter = new RequestMdcFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/v1/api/transcriptions");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<String> seen = new AtomicReference<>();

        filter.doFilter(request, response, (req, res) -> seen.set(MDC.get(MdcKeys.CORRELATION_ID)));

        assertNotNull(seen.get());
        assertFalse(seen.get().isBlank());
        assertEquals(seen.get(), response.getHeader(RequestMdcFilter.REQUEST_ID_HEADER));
        assertNull(MDC.get(MdcKeys.CORRELATION_ID));
    }

    @Test
    @DisplayName("clears MDC even when the chain fails")
    void clearsOnError() {
        RequestMdcFilter filter = new RequestMdcFilter();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/api/orders");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThrows(ServletException.class, () -> filter.doFilter(request, response, (req, res) -> {
            throw new ServletException("boom");
        }));
        assertNull(MDC.get(MdcKeys.CORRELATION_ID));
    }

    @Test
    @DisplayName("skips access logs for actuator, swagger and h2")
    void skipsNoisePaths() {
        assertFalse(RequestMdcFilter.shouldLog("/actuator/health"));
        assertFalse(RequestMdcFilter.shouldLog("/swagger-ui/index.html"));
        assertFalse(RequestMdcFilter.shouldLog("/v3/api-docs"));
        assertFalse(RequestMdcFilter.shouldLog("/h2-console"));
        assertTrue(RequestMdcFilter.shouldLog("/v1/api/orders"));
        assertFalse(RequestMdcFilter.shouldLog(null));
    }
}
