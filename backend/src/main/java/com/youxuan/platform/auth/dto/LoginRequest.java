package com.youxuan.platform.auth.dto;

import javax.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String account;

    @NotBlank
    private String password;

    private String loginType = "USER";

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
}
