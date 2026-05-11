package com.youxuan.gateway.config;

import com.youxuan.common.constant.ApiConstants;
import java.util.Arrays;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GatewayWebConfiguration implements WebMvcConfigurer {

    private final GatewayCorsProperties gatewayCorsProperties;

    public GatewayWebConfiguration(GatewayCorsProperties gatewayCorsProperties) {
        this.gatewayCorsProperties = gatewayCorsProperties;
    }

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
