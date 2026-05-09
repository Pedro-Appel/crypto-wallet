package org.appel.crypto_wallet_manager.web;

import io.micrometer.tracing.TraceContext;
import io.micrometer.tracing.Tracer;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
class TraceIdFilter extends OncePerRequestFilter {

    private final Tracer tracer;

    TraceIdFilter(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable FilterChain filterChain) throws ServletException, IOException {
        String traceId = getTraceId();
        if(filterChain != null && response != null) {
            if (traceId != null) {
                response.setHeader("X-Trace-Id", traceId);
            }
            String spanId = getSpanId();
            if (spanId != null) {
                response.setHeader("X-Span-Id", spanId);
            }
            filterChain.doFilter(request, response);
        }
    }

    private @Nullable String getTraceId() {
        TraceContext context = this.tracer.currentTraceContext().context();
        return context != null ? context.traceId() : null;
    }

    private @Nullable String getSpanId() {
        TraceContext context = this.tracer.currentTraceContext().context();
        return context != null ? context.spanId() : null;
    }

}