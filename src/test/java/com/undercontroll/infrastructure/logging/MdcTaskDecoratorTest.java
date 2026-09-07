package com.undercontroll.infrastructure.logging;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MdcTaskDecoratorTest {

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    @DisplayName("copies the caller MDC into the async runnable")
    void copiesContext() {
        MDC.put(MdcKeys.CORRELATION_ID, "corr-1");
        MDC.put(MdcKeys.USER_ID, "42");
        AtomicReference<String> correlation = new AtomicReference<>();
        AtomicReference<String> userId = new AtomicReference<>();

        Runnable decorated = new MdcTaskDecorator().decorate(() -> {
            correlation.set(MDC.get(MdcKeys.CORRELATION_ID));
            userId.set(MDC.get(MdcKeys.USER_ID));
        });

        MDC.clear();
        decorated.run();

        assertEquals("corr-1", correlation.get());
        assertEquals("42", userId.get());
        assertNull(MDC.get(MdcKeys.CORRELATION_ID));
        assertNull(MDC.get(MdcKeys.USER_ID));
    }

    @Test
    @DisplayName("restores the previous MDC after the runnable")
    void restoresPrevious() {
        MDC.put(MdcKeys.CORRELATION_ID, "caller");
        Runnable decorated = new MdcTaskDecorator().decorate(() ->
                MDC.put(MdcKeys.CORRELATION_ID, "async"));

        MDC.put(MdcKeys.CORRELATION_ID, "worker-previous");
        decorated.run();

        assertEquals("worker-previous", MDC.get(MdcKeys.CORRELATION_ID));
    }
}
