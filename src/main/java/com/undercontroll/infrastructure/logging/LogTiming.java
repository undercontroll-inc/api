package com.undercontroll.infrastructure.logging;

import java.util.concurrent.TimeUnit;

public final class LogTiming {

    private LogTiming() {
    }

    public static long millisSince(long startNanos) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanos);
    }
}
