package com.youxuan.gateway.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 网关 CORS 跨域配置属性
 * <p>
 * 从配置文件读取 {@code youxuan.gateway.cors.allowed-origins} 属性，
 * 允许的域名列表以逗号分隔。默认允许本地前端开发服务器（5173端口）。
 * </p>
 */
@ConfigurationProperties(prefix = "youxuan.gateway.cors")
public class GatewayCorsProperties {

    /** 允许跨域访问的域名列表，多个以逗号分隔，默认包含本地开发环境 */
    private String allowedOrigins = "http://localhost:5173,http://127.0.0.1:5173";

    /** @return 原始配置的逗号分隔字符串 */
    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    /** @param allowedOrigins 允许的域名列表字符串 */
    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * 将配置的字符串解析为 List
     * <p>按逗号分割并去除首尾空格。</p>
     *
     * @return 允许跨域的域名列表
     */
    public List<String> getAllowedOriginList() {
        return Arrays.asList(allowedOrigins.split("\\s*,\\s*"));
    }
}
