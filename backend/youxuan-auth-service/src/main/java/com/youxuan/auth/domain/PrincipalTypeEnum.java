package com.youxuan.auth.domain;

/**
 * 主体类型枚举。
 * <p>
 * 定义系统支持的身份主体类型，用于区分不同角色入口的认证与授权逻辑。
 * 每个主体类型对应不同的认证流程和权限体系。
 * </p>
 */
public enum PrincipalTypeEnum {

    /** 普通用户（C 端消费者） */
    USER,

    /** 商家员工（商家后台操作人员） */
    MERCHANT_STAFF,

    /** 平台管理员（平台后台运营人员） */
    PLATFORM_ADMIN
}
