package com.youxuan.gateway.dto;

/**
 * 服务目录 DTO
 * <p>
 * 描述平台中一个微服务的基本信息，包括编码、名称、访问路径和职责说明。
 * 用于服务目录展示和开发者参考。
 * </p>
 */
public class ServiceDirectoryDTO {

    /** 服务编码，如 auth、product、order */
    private String serviceCode;
    /** 服务中文名称 */
    private String serviceName;
    /** 服务的基础访问路径 */
    private String basePath;
    /** 服务职责描述 */
    private String responsibility;

    /**
     * 构造服务目录条目
     *
     * @param serviceCode   服务编码
     * @param serviceName   服务中文名称
     * @param basePath      服务基础路径
     * @param responsibility 职责描述
     */
    public ServiceDirectoryDTO(String serviceCode, String serviceName, String basePath, String responsibility) {
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.basePath = basePath;
        this.responsibility = responsibility;
    }

    /** @return 服务编码 */
    public String getServiceCode() {
        return serviceCode;
    }

    /** @param serviceCode 服务编码 */
    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    /** @return 服务中文名称 */
    public String getServiceName() {
        return serviceName;
    }

    /** @param serviceName 服务中文名称 */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /** @return 服务基础路径 */
    public String getBasePath() {
        return basePath;
    }

    /** @param basePath 服务基础路径 */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    /** @return 服务职责描述 */
    public String getResponsibility() {
        return responsibility;
    }

    /** @param responsibility 服务职责描述 */
    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }
}
