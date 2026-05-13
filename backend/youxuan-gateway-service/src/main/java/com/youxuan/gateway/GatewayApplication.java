package com.youxuan.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 网关服务（youxuan-gateway-service）启动类
 * <p>
 * API 网关是整个平台的统一入口，负责：
 * <ul>
 *   <li>路由转发：将前端请求分发到各微服务（基于 Spring Cloud Gateway）</li>
 *   <li>CORS 跨域处理</li>
 *   <li>健康检查和服务目录展示</li>
 * </ul>
 * 排除与 Servlet 容器绑定的自动配置类（Gateway 基于 WebFlux 响应式栈），
 * 包括 {@code CommonWebAutoConfiguration} 和 {@code JwtAutoConfiguration}。
 * </p>
 */
@SpringBootApplication(
    scanBasePackages = {"com.youxuan.gateway", "com.youxuan.common"},
    excludeName = {
        "com.youxuan.common.config.CommonWebAutoConfiguration",
        "com.youxuan.common.security.JwtAutoConfiguration"
    }
)
@ConfigurationPropertiesScan
public class GatewayApplication {

    /**
     * 网关服务启动入口
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
