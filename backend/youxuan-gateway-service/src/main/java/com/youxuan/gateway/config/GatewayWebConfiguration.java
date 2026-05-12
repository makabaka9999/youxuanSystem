package com.youxuan.gateway.config;

import org.springframework.context.annotation.Configuration;

/**
 * 网关 Web 配置
 * <p>
 * CORS 跨域策略已统一迁移至 {@code application.yml} 的
 * {@code spring.cloud.gateway.globalcors} 配置项，对所有路由（包括网关自身端点）
 * 统一生效，避免 WebFlux 层与 Gateway 层双层 CORS 处理冲突。
 * </p>
 */
@Configuration
public class GatewayWebConfiguration {

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
}
