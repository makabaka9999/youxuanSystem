package com.youxuan.common.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 认证自动配置。
 * 通过 youxuan.auth.jwt.secret 配置签名密钥，默认关闭。
 * 设置 youxuan.auth.jwt.enabled=true 即可启用 JWT 过滤链。
 */
@Configuration
@ConditionalOnProperty(prefix = "youxuan.auth.jwt", name = "enabled", havingValue = "true")
public class JwtAutoConfiguration {

    /** JWT 签名密钥，必须与应用 auth-service 的 youxuan.auth.security.secret 保持一致 */
    @Value("${youxuan.auth.jwt.secret:replace-with-at-least-32-bytes-secret}")
    private String jwtSecret;

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenValidator jwtTokenValidator() {
        return new JwtTokenValidator(jwtSecret);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilter(
            JwtTokenValidator jwtTokenValidator) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new JwtAuthenticationFilter(jwtTokenValidator));
        registration.addUrlPatterns("/*");
        registration.setOrder(-100);
        return registration;
    }
}
