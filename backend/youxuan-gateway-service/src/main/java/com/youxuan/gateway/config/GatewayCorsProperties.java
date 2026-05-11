package com.youxuan.gateway.config;

import java.util.Arrays;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "youxuan.gateway.cors")
public class GatewayCorsProperties {

    private String allowedOrigins = "http://localhost:5173,http://127.0.0.1:5173";

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOriginList() {
        return Arrays.asList(allowedOrigins.split("\\s*,\\s*"));
    }
}
