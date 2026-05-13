package com.youxuan.merchant.controller;

import com.youxuan.common.api.ApiResponse;
import com.youxuan.common.web.RequestContext;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口。
 */
@RestController
@RequestMapping("/api/v1/merchant/upload")
public class UploadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Value("${youxuan.upload.path:}")
    private String uploadPath;

    /** 实际的绝对路径 */
    private String resolvedPath;

    @PostConstruct
    public void init() {
        if (uploadPath == null || uploadPath.trim().isEmpty()) {
            resolvedPath = System.getProperty("user.dir") + File.separator + "uploads";
        } else {
            File f = new File(uploadPath);
            resolvedPath = f.isAbsolute() ? uploadPath : System.getProperty("user.dir") + File.separator + uploadPath;
        }
        LOGGER.info("文件上传目录: {}", resolvedPath);
    }

    /**
     * 上传图片，返回可访问的 URL。
     */
    @PostMapping("/image")
    public ApiResponse<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ApiResponse.fail("PARAM_INVALID", "文件不能为空", RequestContext.getRequestId());
        }

        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + ext;

        try {
            File dir = new File(resolvedPath);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(dir, fileName);
            file.transferTo(dest);

            Map<String, String> data = new HashMap<>();
            data.put("url", "/uploads/" + fileName);
            data.put("originalName", originalName);
            return ApiResponse.success(data, RequestContext.getRequestId());
        } catch (IOException e) {
            LOGGER.error("文件上传失败", e);
            return ApiResponse.fail("INTERNAL_ERROR", "文件上传失败", RequestContext.getRequestId());
        }
    }
}
