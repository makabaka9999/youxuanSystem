package com.youxuan.gateway.dto;

/**
 * 网关健康状态 DTO
 * <p>
 * 用于封装健康检查接口的返回信息，包括服务运行状态、名称、API前缀和架构类型。
 * </p>
 */
public class GatewayHealthDTO {

    /** 服务运行状态，如 "UP" 表示正常运行 */
    private String status;
    /** 服务名称 */
    private String serviceName;
    /** API 统一前缀路径 */
    private String apiPrefix;
    /** 架构类型，如 "microservices" */
    private String architecture;

    /** @return 服务运行状态 */
    public String getStatus() {
        return status;
    }

    /** @param status 服务运行状态 */
    public void setStatus(String status) {
        this.status = status;
    }

    /** @return 服务名称 */
    public String getServiceName() {
        return serviceName;
    }

    /** @param serviceName 服务名称 */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /** @return API 统一前缀路径 */
    public String getApiPrefix() {
        return apiPrefix;
    }

    /** @param apiPrefix API 统一前缀路径 */
    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
    }

    /** @return 架构类型 */
    public String getArchitecture() {
        return architecture;
    }

    /** @param architecture 架构类型 */
    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }
}
