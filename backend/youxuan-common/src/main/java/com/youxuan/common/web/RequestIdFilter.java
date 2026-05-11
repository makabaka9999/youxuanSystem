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

/**
 * 请求追踪ID过滤器
 * <p>
 * 继承 {@link OncePerRequestFilter}，确保每个请求仅执行一次过滤逻辑。
 * 从 HTTP 请求头中提取 X-Request-Id，若调用方未提供则自动生成 UUID；
 * 将请求ID存入 {@link RequestContext}（供业务代码使用）和 SLF4J MDC（供日志输出使用），
 * 并在响应头中回传 X-Request-Id。请求结束后自动清理上下文。
 * </p>
 */
public class RequestIdFilter extends OncePerRequestFilter {

    /** MDC 中请求追踪ID的键名 */
    private static final String MDC_REQUEST_ID = "requestId";

    /**
     * 执行过滤逻辑
     * <p>
     * 优先从请求头中读取 X-Request-Id；若不存在则自动生成 UUID（去掉横线）。
     * 将请求ID分别设置到 {@link RequestContext} 和 MDC 中，并在响应头中写回。
     * 使用 try/finally 确保请求结束后释放 ThreadLocal 和 MDC 资源。
     * </p>
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求头获取 X-Request-Id，若为空则自动生成
        String requestId = request.getHeader(ApiConstants.REQUEST_ID_HEADER);
        if (!StringUtils.hasText(requestId)) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        // 注入到 ThreadLocal 和日志 MDC 中
        RequestContext.setRequestId(requestId);
        MDC.put(MDC_REQUEST_ID, requestId);
        // 在响应头中回写请求追踪ID
        response.setHeader(ApiConstants.REQUEST_ID_HEADER, requestId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            // 请求结束后清理，防止 ThreadLocal 内存泄漏
            MDC.remove(MDC_REQUEST_ID);
            RequestContext.clear();
        }
    }
}
