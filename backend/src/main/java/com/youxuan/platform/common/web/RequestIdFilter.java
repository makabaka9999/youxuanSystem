package com.youxuan.platform.common.web;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = request.getHeader(RequestIdHolder.HEADER_NAME);
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        RequestIdHolder.set(requestId);
        MDC.put("requestId", requestId);
        response.setHeader(RequestIdHolder.HEADER_NAME, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
            RequestIdHolder.clear();
        }
    }
}
