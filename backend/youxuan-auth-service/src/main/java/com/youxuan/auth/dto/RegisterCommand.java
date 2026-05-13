package com.youxuan.auth.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 注册请求 DTO。
 */
public class RegisterCommand {

    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    private String mobile;

    /** 昵称 */
    private String nickname;

    /** 密码（RSA 加密后的密文） */
    @NotBlank(message = "密码不能为空")
    private String password;

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
