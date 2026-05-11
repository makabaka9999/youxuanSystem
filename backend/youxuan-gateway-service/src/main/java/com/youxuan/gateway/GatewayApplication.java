package com.youxuan.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 网关服务（youxuan-gateway-service）启动类
 * <p>
 * API 网关是整个平台的统一入口，负责：
 * <ul>
 *   <li>路由转发：将前端请求分发到各微服务</li>
 *   <li>CORS 跨域处理</li>
 *   <li>健康检查和服务目录展示</li>
 * </ul>
 * 扫描 {@code com.youxuan.gateway} 和 {@code com.youxuan.common} 两个包，
 * 以便加载网关自身的 Bean 以及通用模块的自动配置。
 * </p>
 */
@SpringBootApplication(scanBasePackages = {"com.youxuan.gateway", "com.youxuan.common"})
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
