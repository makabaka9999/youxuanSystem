package com.youxuan.gateway.dto;

public class GatewayHealthDTO {

    private String status;
    private String serviceName;
    private String apiPrefix;
    private String architecture;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getApiPrefix() {
        return apiPrefix;
    }

    public void setApiPrefix(String apiPrefix) {
        this.apiPrefix = apiPrefix;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }
}
