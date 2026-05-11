package com.youxuan.common.config;

import com.youxuan.common.web.RequestIdFilter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公共 Web 模块自动配置类
 * <p>
 * 自动注册 {@link RequestIdFilter} 到过滤器链中，确保每个请求都拥有唯一的请求追踪 ID。
 * 需要在 Spring MVC 自动配置之前注册，以保证过滤器优先级最高。
 * </p>
 */
@Configuration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class CommonWebAutoConfiguration {

    /**
     * 注册请求追踪ID过滤器
     * <p>
     * 优先级设为 {@link Integer#MIN_VALUE}，确保该过滤器最先执行；
     * 拦截所有请求路径 /*，为每个请求注入 X-Request-Id。
     * </p>
     *
     * @return 过滤器注册 Bean
     */
    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilterRegistrationBean() {
        FilterRegistrationBean<RequestIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestIdFilter());
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
