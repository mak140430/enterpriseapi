package com.example.enterpriseapi.util;

import org.slf4j.MDC;
import java.util.UUID;

public class CorrelationIdUtils {
    private static final String CORRELATION_ID_KEY = "correlationId";

    public static String getCurrentCorrelationId() {
        String correlationId = MDC.get(CORRELATION_ID_KEY);
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = generateCorrelationId();
            MDC.put(CORRELATION_ID_KEY, correlationId);
        }
        return correlationId;
    }

    public static void setCorrelationId(String correlationId) {
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = generateCorrelationId();
        }
        MDC.put(CORRELATION_ID_KEY, correlationId);
    }

    public static void clearCorrelationId() {
        MDC.remove(CORRELATION_ID_KEY);
    }

    private static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
} 