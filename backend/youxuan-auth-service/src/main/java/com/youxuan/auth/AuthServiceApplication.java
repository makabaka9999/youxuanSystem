package com.youxuan.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 认证服务启动类。
 * <p>
 * 负责启动 Spring Boot 应用，并自动扫描认证模块与公共模块的组件。
 * </p>
 */
@SpringBootApplication(scanBasePackages = {"com.youxuan.auth", "com.youxuan.common"})
@ConfigurationPropertiesScan
public class AuthServiceApplication {

    /**
     * 应用入口方法。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
