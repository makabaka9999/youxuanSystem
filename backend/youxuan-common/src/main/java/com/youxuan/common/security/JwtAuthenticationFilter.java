package com.youxuan.common.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * JWT 认证过滤器，从 Authorization Header 中提取 Bearer Token 并验证。
 * 验证通过后将 JwtClaims 写入 RequestContext，供后续业务代码使用。
 * 放行路径：/api/v1/health、/api/v1/auth/**、/actuator/**
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    /** 白名单路径前缀，这些路径不需要 JWT 认证 */
    private static final String[] PUBLIC_PATHS = {
            "/api/v1/health",
            "/api/v1/auth/",
            "/api/v1/categories/",
            "/actuator/"
    };

    /** JWT 验证器 */
    private final JwtTokenValidator tokenValidator;

    public JwtAuthenticationFilter(JwtTokenValidator tokenValidator) {
        this.tokenValidator = tokenValidator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // 白名单路径直接放行
        if (isPublicPath(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从 Header 中提取 Bearer Token
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header, uri: {}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"AUTH_REQUIRED\",\"message\":\"未登录或 Token 为空\",\"requestId\":\"\",\"data\":null}");
            return;
        }

        String accessToken = authHeader.substring(7);
        JwtClaims claims = tokenValidator.validate(accessToken);
        if (claims == null) {
            log.warn("Invalid or expired JWT, uri: {}", requestUri);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":\"TOKEN_EXPIRED\",\"message\":\"Token 无效或已过期\",\"requestId\":\"\",\"data\":null}");
            return;
        }

        // 将认证信息写入请求上下文
        JwtRequestContext.set(claims);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束后清理 ThreadLocal，防止内存泄漏
            JwtRequestContext.clear();
        }
    }

    private boolean isPublicPath(String uri) {
        for (String path : PUBLIC_PATHS) {
            if (uri.startsWith(path)) {
                return true;
            }
        }
        return false;
    }
}
