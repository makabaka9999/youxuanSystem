package com.youxuan.gateway.dto;

public class ServiceDirectoryDTO {

    private String serviceCode;
    private String serviceName;
    private String basePath;
    private String responsibility;

    public ServiceDirectoryDTO(String serviceCode, String serviceName, String basePath, String responsibility) {
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.basePath = basePath;
        this.responsibility = responsibility;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }
}
