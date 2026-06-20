package com.example.ailearning.common.log;

import org.slf4j.MDC;

import java.util.UUID;

public final class TraceIdHolder {
    public static final String TRACE_ID = "traceId";

    private TraceIdHolder() {
    }

    public static String init(String incomingTraceId) {
        String traceId = incomingTraceId == null || incomingTraceId.isBlank()
                ? UUID.randomUUID().toString()
                : incomingTraceId;
        MDC.put(TRACE_ID, traceId);
        return traceId;
    }

    public static String getTraceId() {
        String traceId = MDC.get(TRACE_ID);
        return traceId == null ? "" : traceId;
    }

    public static void clear() {
        MDC.remove(TRACE_ID);
    }
}
