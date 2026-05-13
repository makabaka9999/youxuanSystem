package com.youxuan.merchant.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.io.File;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 静态资源配置 — 将上传目录映射为 /uploads/** 可访问。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebMvcConfig.class);

    @Value("${youxuan.upload.path:}")
    private String uploadPath;

    private String resolvedPath;

    @PostConstruct
    public void init() {
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            resolvedPath = System.getProperty("user.dir") + File.separator + "uploads";
        } else {
            File f = new File(uploadPath);
            resolvedPath = f.isAbsolute() ? uploadPath : System.getProperty("user.dir") + File.separator + uploadPath;
        }
        LOGGER.info("静态资源目录: {}", resolvedPath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + resolvedPath + "/");
    }
}
