package com.youxuan.auth.dto;

import javax.validation.constraints.NotBlank;

public class LoginCommand {

    @NotBlank
    private String account;

    @NotBlank
    private String password;

    @NotBlank
    private String principalType;

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

    public String getPrincipalType() {
        return principalType;
    }

    public void setPrincipalType(String principalType) {
        this.principalType = principalType;
    }
}
