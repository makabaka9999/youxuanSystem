package com.youxuan.auth.dto;

import javax.validation.constraints.NotBlank;

/**
 * 登录命令 DTO。
 * <p>
 * 封装用户登录时提交的请求参数，包含账号、密码和主体类型。
 * 所有字段均不能为空，由 javax.validation 进行校验。
 * 密码字段在传输前需使用 RSA 公钥加密。
 * </p>
 */
public class LoginCommand {

    /** 登录账号（用户手机号 / 管理员用户名） */
    @NotBlank
    private String account;

    /** 登录密码（RSA 加密后的 Base64 字符串） */
    @NotBlank
    private String password;

    /** 主体类型（USER / MERCHANT_STAFF / PLATFORM_ADMIN） */
    @NotBlank
    private String principalType;

    /**
     * 获取登录账号。
     *
     * @return 登录账号
     */
    public String getAccount() {
        return account;
    }

    /**
     * 设置登录账号。
     *
     * @param account 登录账号
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * 获取登录密码（RSA 加密后的 Base64 字符串）。
     *
     * @return 加密后的密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置登录密码。
     *
     * @param password RSA 加密后的 Base64 密码字符串
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取主体类型。
     *
     * @return 主体类型字符串
     */
    public String getPrincipalType() {
        return principalType;
    }

    /**
     * 设置主体类型。
     *
     * @param principalType 主体类型字符串
     */
    public void setPrincipalType(String principalType) {
        this.principalType = principalType;
    }
}
