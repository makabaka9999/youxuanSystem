package com.youxuan.gateway.config;

import com.youxuan.common.constant.ApiConstants;
import java.util.Arrays;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 网关 Web 配置
 * <p>
 * 实现 {@link WebMvcConfigurer} 接口，配置跨域（CORS）策略。
 * 允许前端应用跨域调用后端 API，同时限制允许的请求头和请求方法以确保安全。
 * </p>
 */
@Configuration
public class GatewayWebConfiguration implements WebMvcConfigurer {

    /** CORS 配置属性 */
    private final GatewayCorsProperties gatewayCorsProperties;

    /**
     * 构造 Web 配置
     *
     * @param gatewayCorsProperties CORS 配置属性
     */
    public GatewayWebConfiguration(GatewayCorsProperties gatewayCorsProperties) {
        this.gatewayCorsProperties = gatewayCorsProperties;
    }

    /**
     * 配置 CORS 跨域规则
     * <p>
     * 对 /api/** 路径生效，允许配置的域名来源；
     * 支持的请求方法包括 RESTful 常用方法及 OPTIONS 预检；
     * 允许携带 Authorization、Content-Type、X-Request-Id 等自定义头；
     * 预检请求缓存 1800 秒（30分钟）。
     * </p>
     *
     * @param registry CORS 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(gatewayCorsProperties.getAllowedOriginList().toArray(new String[0]))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders(
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.CONTENT_TYPE,
                        ApiConstants.REQUEST_ID_HEADER,
                        ApiConstants.IDEMPOTENCY_KEY_HEADER,
                        "X-Timestamp",
                        "X-Signature")
                .exposedHeaders(ApiConstants.REQUEST_ID_HEADER)
                .allowCredentials(true)
                .maxAge(1800L);
    }
}
