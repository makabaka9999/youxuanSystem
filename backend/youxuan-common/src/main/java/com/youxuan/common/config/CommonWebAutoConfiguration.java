package com.youxuan.common.config;

import com.youxuan.common.web.RequestIdFilter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class CommonWebAutoConfiguration {

    @Bean
    public FilterRegistrationBean<RequestIdFilter> requestIdFilterRegistrationBean() {
        FilterRegistrationBean<RequestIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestIdFilter());
        registrationBean.setOrder(Integer.MIN_VALUE);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
