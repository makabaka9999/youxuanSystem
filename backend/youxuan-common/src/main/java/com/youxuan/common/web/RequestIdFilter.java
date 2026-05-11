package com.youxuan.common.web;

import com.youxuan.common.constant.ApiConstants;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class RequestIdFilter extends OncePerRequestFilter {

    private static final String MDC_REQUEST_ID = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestId = request.getHeader(ApiConstants.REQUEST_ID_HEADER);
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        RequestContext.setRequestId(requestId);
        MDC.put(MDC_REQUEST_ID, requestId);
        response.setHeader(ApiConstants.REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_REQUEST_ID);
            RequestContext.clear();
        }
    }
}
